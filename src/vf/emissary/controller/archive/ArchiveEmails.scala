package vf.emissary.controller.archive

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
import vf.emissary.database.access.single.messaging.message_thread.DbMessageThread
import vf.emissary.database.model.messaging.SubjectStatementLinkModel
import vf.emissary.model.partial.messaging.SubjectStatementLinkData

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
	
	private val subjectPrefixRegex = Regex.startOfLine +
		(Regex.upperCaseLetter + Regex.letter + Regex.escape(':') + Regex.whiteSpace)
			.withinParenthesis.oneOrMoreTimes
	
	
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
						val subjectStatements = DbStatements.store(email.subject.drop(baseSubjectStartIndex))
						// SubjectStatementLinkModel.insert(subjectStatements.map { s => SubjectStatementLinkData(???, s.either.id) })
						
						// TODO: identify message thread
						
						// Checks whether this email exists already (compares sender, send time and message id)
						// TODO: Continue
					}
				}
			}
	}
}
