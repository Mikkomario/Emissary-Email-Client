package vf.emissary.controller.archive

import org.jsoup.Jsoup
import utopia.courier.controller.read.{EmailReader, TargetFolders}
import utopia.courier.model.read.DeletionRule.{DeleteProcessed, NeverDelete}
import utopia.courier.model.read.ReadSettings
import utopia.flow.collection.CollectionExtensions._
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
import vf.emissary.database.model.messaging.{MessageStatementLinkModel, SubjectStatementLinkModel}
import vf.emissary.model.partial.messaging.{MessageStatementLinkData, SubjectStatementLinkData}

import java.nio.file.Path
import java.time.Instant
import scala.concurrent.ExecutionContext
import scala.collection.mutable

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
	
	def apply(attachmentStoreDirectory: Path, since: Option[Instant] = None, deleteArchivedEmails: Boolean = false)
	         (implicit exc: ExecutionContext, cPool: ConnectionPool, readSettings: ReadSettings) =
	{
		// Reads email information
		EmailReader.filteredDefaultWithAttachments(attachmentStoreDirectory) { h => since.forall { h.sendTime > _ } }
			.iterateAsync(TargetFolders.all,
				deletionRule = if (deleteArchivedEmails) DeleteProcessed else NeverDelete) { messagesIter =>
				// Queues unresolved message ids associated with existing threads
				val unresolvedThreadIdPerMessageId = mutable.Map[String, Int]()
				cPool.tryWith { implicit c =>
					// Terminates this process if message-parsing fails at some point
					messagesIter.takeTo { _.isFailure }.mapSuccesses { email =>
						// Finds message sender id
						val senderId = DbAddress(email.sender.addressPart).pullOrInsertId()
						// Assigns sender name, if appropriate
						email.sender.namePart.notEmpty.foreach { name =>
							DbAddress(senderId.either).assignName(name, selfAssigned = true)
						}
						
						// Checks whether the message is associated with any existing thread
						val existingThreadId = email.messageId.notEmpty.flatMap(unresolvedThreadIdPerMessageId.get)
							.orElse {
								val refs = email.inReplyTo.notEmpty match {
									case Some(parentId) => email.references.appendIfDistinct(parentId)
									case None => email.references
								}
								NotEmpty(refs).flatMap { refs =>
									refs.findMap(unresolvedThreadIdPerMessageId.get).orElse {
										DbMessages.withMessageIds(refs).threadIds.headOption
									}
								}
							}
						email.messageId.notEmpty.foreach(unresolvedThreadIdPerMessageId.remove)
						
						// Creates a new thread, if appropriate
						val threadId = existingThreadId.getOrElse { DbMessageThread.newId() }
						// "Re:" etc. initials are removed from the subject before storing it
						val baseSubjectStartIndex = subjectPrefixRegex
							.endIndexIteratorIn(email.subject).nextOption.getOrElse(0)
						val subject = email.subject.drop(baseSubjectStartIndex).notEmpty
							.map { DbSubject.store(_).either }
						subject.foreach { s => DbMessageThread(threadId).assignSubject(s.id) }
						
						// Checks whether this email exists already (compares thread, sender, send time and message id)
						val messageId = DbMessage(threadId, email.messageId, senderId.either, email.sendTime)
							.pullOrInsertId()
						// For new messages, writes message contents and assigns attachments
						messageId.leftOption.foreach { messageId =>
							// Removes the HTML content, as well as any reference to an earlier message
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
								val statementIds = DbStatements.store(processedEmailText).map { _.either.id }
								MessageStatementLinkModel
									.insert(statementIds.zipWithIndex.map { case (statementId, index) =>
										MessageStatementLinkData(messageId, statementId, index)
									})
							}
							
							// TODO: Add attachments
						}
					}
				}
			}
	}
}
