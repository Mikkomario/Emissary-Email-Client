package vf.emissary.controller.archive

import org.jsoup.Jsoup
import utopia.courier.controller.read.{EmailReader, TargetFolders}
import utopia.courier.model.read.DeletionRule.{DeleteProcessed, NeverDelete}
import utopia.courier.model.read.ReadSettings
import utopia.flow.collection.CollectionExtensions._
import utopia.flow.parse.file.FileExtensions._
import utopia.flow.parse.string.Regex
import utopia.flow.time.TimeExtensions._
import utopia.flow.util.NotEmpty
import utopia.flow.util.StringExtensions._
import utopia.vault.database.ConnectionPool
import vf.emissary.database.access.many.messaging.message.DbMessages
import vf.emissary.database.access.many.text.statement.DbStatements
import vf.emissary.database.access.single.messaging.address.DbAddress
import vf.emissary.database.access.single.messaging.message.DbMessage
import vf.emissary.database.access.single.messaging.message_thread.DbMessageThread
import vf.emissary.database.access.single.messaging.subject.DbSubject
import vf.emissary.database.model.messaging.{AttachmentModel, MessageStatementLinkModel}
import vf.emissary.model.partial.messaging.{AttachmentData, MessageStatementLinkData}

import java.nio.file.Path
import java.time.Instant
import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.util.Success

/**
 * An interface used for storing read email information to the database
 * @author Mikko Hilpinen
 * @since 13.10.2023, v0.1
 */
object ArchiveEmails
{
	// ATTRIBUTES   -----------------------
	
	private lazy val subjectPrefixRegex = Regex.startOfLine +
		(Regex.upperCaseLetter + Regex.letter + Regex.escape(':') + Regex.whiteSpace)
			.withinParenthesis.oneOrMoreTimes
	
	private lazy val zonerReplyLineRegex = Regex.escape('>') + Regex.whiteSpace + Regex.any
	private lazy val replyHeaderRegex = Regex.letter + Regex.letter.oneOrMoreTimes + Regex.escape(':') +
		Regex.whiteSpace + Regex.any
	private lazy val anyReplyLineRegex = zonerReplyLineRegex.withinParenthesis || replyHeaderRegex.withinParenthesis
	
	
	// OTHER    ---------------------------
	
	/**
	 * Reads and archives emails. Interrupts the archiving process if a failure is encountered.
	 * @param attachmentStoreDirectory Directory where email attachments should be stored
	 * @param since Earliest included message time. None if all messages should be included (default).
	 * @param deleteArchivedEmails Whether archived emails should be deleted from the email server. Default = false.
	 * @param exc Implicit execution context
	 * @param cPool Implicit connection pool
	 * @param readSettings Settings for reading email
	 * @return Future that resolves into a success or a failure once the archiving process has completed.
	 */
	def apply(attachmentStoreDirectory: Path, since: Option[Instant] = None, deleteArchivedEmails: Boolean = false)
	         (implicit exc: ExecutionContext, cPool: ConnectionPool, readSettings: ReadSettings) =
	{
		// TODO: Remove test prints
		// Reads email information
		EmailReader.filteredDefaultWithAttachments(attachmentStoreDirectory) { h => since.forall { h.sendTime > _ } }
			.iterateAsync(TargetFolders.all,
				deletionRule = if (deleteArchivedEmails) DeleteProcessed else NeverDelete) { messagesIter =>
				// Queues unresolved message ids associated with existing threads
				val unresolvedThreadIdPerMessageId = mutable.Map[String, Int]()
				// Also queues replyTo values (referencing message db id -> referenced message id)
				// As well as message ids in general
				val messageIds = mutable.Map[String, Int]()
				val unresolvedReplyReferences = mutable.Map[String, Set[Int]]()
				
				cPool.tryWith { implicit c =>
					// Terminates this process if message-parsing fails at some point
					messagesIter
						// TODO: Remove this test limitation
						.take(20)
						.mapSuccesses { email =>
							println("\n-----------------------")
							println(s"Processing email from ${email.sender.addressPart} (${
								email.sender.namePart}), received at ${email.sendTime.toLocalDateTime}")
							
							// Finds message sender id
							val senderId = DbAddress(email.sender.addressPart).pullOrInsertId()
							// Assigns sender name, if appropriate
							email.sender.namePart.notEmpty.foreach { name =>
								DbAddress(senderId.either).assignName(name, selfAssigned = true)
							}
							println(s"Sender id: $senderId")
							
							val refs = (email.inReplyTo.notEmpty match {
								case Some(parentId) => email.references.appendIfDistinct(parentId)
								case None => email.references
							}).toSet
							// Checks whether the message is associated with any existing thread
							val existingThreadId = email.messageId.notEmpty.flatMap(unresolvedThreadIdPerMessageId.get)
								.orElse {
									NotEmpty(refs).flatMap { refs =>
										refs.findMap(unresolvedThreadIdPerMessageId.get).orElse {
											DbMessages.withMessageIds(refs).threadIds.headOption
										}
									}
								}
							email.messageId.notEmpty.foreach(unresolvedThreadIdPerMessageId.remove)
							println(s"Message id: ${email.messageId}")
							println(s"References: ${refs.mkString(", ")}")
							println(s"Existing thread id: $existingThreadId")
							
							// Creates a new thread, if appropriate
							val threadId = existingThreadId.getOrElse { DbMessageThread.newId() }
							// Remembers unresolved thread message ids
							(refs -- messageIds.keySet).foreach { unresolvedThreadIdPerMessageId(_) = threadId }
							
							// "Re:" etc. initials are removed from the subject before storing it
							val baseSubjectStartIndex = subjectPrefixRegex
								.endIndexIteratorIn(email.subject).nextOption.getOrElse(0)
							val subject = email.subject.drop(baseSubjectStartIndex).notEmpty
								.map { DbSubject.store(_).either }
							subject.foreach { s => DbMessageThread(threadId).assignSubject(s.id) }
							println(s"Original subject: ${email.subject}")
							println(s"Processed subject: ${email.subject.drop(baseSubjectStartIndex)}")
							
							// Checks whether this email exists already (compares thread, sender, send time and message id)
							val messageId = DbMessage(threadId, email.messageId, senderId.either, email.sendTime)
								.pullOrInsertId()
							// Remembers message id
							messageIds(email.messageId) = messageId.either
							// For new messages, writes message contents and assigns attachments
							messageId.leftOption.foreach { messageId =>
								// Resolves cases where mails need to reference this message
								unresolvedReplyReferences.remove(email.messageId).foreach { referencingIds =>
									println(s"Assigns this message ($messageId) as parent of ${referencingIds.size} messages that were stored earlier")
									DbMessages(referencingIds).replyToIds = messageId
								}
								
								// Removes the HTML content, as well as any reference to an earlier message
								// TODO: Should not remove reply data in situations where the reply message is not available elsewhere
								val emailLines = Jsoup.parse(email.message).body().text().linesIterator.toVector
								val firstReplyLineIndex = emailLines.indices.find { i =>
									anyReplyLineRegex(emailLines(i)) &&
										((i + 1) to (i + 3))
											.forall { i => emailLines.lift(i).exists { anyReplyLineRegex.apply } }
								}
								val processedEmailText = (firstReplyLineIndex match {
									case Some(i) => emailLines.take(i)
									case None => emailLines
								}).mkString("\n")
								
								// Assigns the message text
								processedEmailText.notEmpty.foreach { text =>
									println(s"Processed text:")
									println(processedEmailText)
									val statementIds = DbStatements.store(text).map { _.either.id }
									MessageStatementLinkModel
										.insert(statementIds.zipWithIndex.map { case (statementId, index) =>
											MessageStatementLinkData(messageId, statementId, index)
										})
								}
								
								// Adds attachments, also
								if (email.attachmentPaths.nonEmpty)
									AttachmentModel.insert(
										email.attachmentPaths.map { p => AttachmentData(messageId, p.fileName) })
								
								// Remembers reply reference, unless it can be filled immediately
								email.inReplyTo.notEmpty.foreach { replyMessageId =>
									messageIds.get(replyMessageId) match {
										// Case: Referenced message has already been inserted => Adds a reference to it
										case Some(replyId) =>
											println(s"Assigns replyId $replyId to message $messageId")
											DbMessage(messageId).replyToId = replyId
										// Case: Referenced message hasn't yet been inserted => Remembers the missing link
										case None => unresolvedReplyReferences.updateWith(replyMessageId) {
											case Some(referencingMessageIds) => Some(referencingMessageIds + messageId)
											case None => Some(Set(messageId))
										}
									}
								}
							}
						}
						.find { _.isFailure }.getOrElse { Success(()) }
				}.flatten
			}
	}
}
