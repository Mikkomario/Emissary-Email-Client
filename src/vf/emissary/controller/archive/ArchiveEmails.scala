package vf.emissary.controller.archive

import org.jsoup.Jsoup
import utopia.courier.controller.read.{EmailReader, TargetFolders}
import utopia.courier.model.Email
import utopia.courier.model.read.DeletionRule.{DeleteProcessed, NeverDelete}
import utopia.courier.model.read.ReadSettings
import utopia.flow.collection.CollectionExtensions._
import utopia.flow.parse.file.FileConflictResolution.Rename
import utopia.flow.parse.file.FileExtensions._
import utopia.flow.parse.string.Regex
import utopia.flow.time.TimeExtensions._
import utopia.flow.util.StringExtensions._
import utopia.flow.util.{NotEmpty, TryCatch}
import utopia.vault.database.{Connection, ConnectionPool}
import vf.emissary.database.access.many.messaging.address.DbAddresses
import vf.emissary.database.access.many.messaging.address_name.DbAddressNames
import vf.emissary.database.access.many.messaging.message.DbMessages
import vf.emissary.database.access.many.text.statement.DbStatements
import vf.emissary.database.access.single.messaging.message.DbMessage
import vf.emissary.database.access.single.messaging.message_thread.DbMessageThread
import vf.emissary.database.access.single.messaging.subject.DbSubject
import vf.emissary.database.model.messaging._
import vf.emissary.model.partial.messaging._

import java.nio.file.Path
import java.time.{Instant, LocalDate}
import scala.annotation.tailrec
import scala.collection.immutable.VectorBuilder
import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

/**
 * An interface used for storing read email information to the database
 * @author Mikko Hilpinen
 * @since 13.10.2023, v0.1
 */
object ArchiveEmails
{
	// ATTRIBUTES   -----------------------
	
	private lazy val multiWhiteSpaceRegex = Regex.whiteSpace + Regex.whiteSpace.oneOrMoreTimes
	private lazy val manyNewLinesRegex = Regex.newLine.times(3) + Regex.newLine.anyTimes
	private lazy val escapedNewLineRegex = Regex.backslash + Regex("n")
	
	private lazy val subjectPrefixRegex = Regex.startOfLine +
		(Regex.upperCaseLetter + Regex.letter + Regex.escape(':') + Regex.whiteSpace)
			.withinParenthesis.oneOrMoreTimes
	
	private lazy val zonerReplyLineRegex = Regex.escape('>') + Regex.any
	private lazy val replyHeaderRegex = Regex.letter + Regex.letter.oneOrMoreTimes + Regex.escape(':') +
		Regex.whiteSpace + Regex.any
	private lazy val anyReplyLineRegex = zonerReplyLineRegex.withinParenthesis || replyHeaderRegex.withinParenthesis
	
	private lazy val dashRegex = Regex.escape('-')
	private lazy val multiDashRegex = dashRegex + dashRegex.oneOrMoreTimes
	
	private lazy val validFileNamePartRegex = (Regex.letterOrDigit || dashRegex).withinParenthesis.oneOrMoreTimes
	
	
	// OTHER    ---------------------------
	
	/**
	 * Reads and archives emails.
	 * @param attachmentStoreDirectory Directory where email attachments should be stored
	 * @param since Earliest included message time. None if all messages should be included (default).
	 * @param deleteArchivedEmails Whether archived emails should be deleted from the email server. Default = false.
	 * @param exc Implicit execution context
	 * @param cPool Implicit connection pool
	 * @param readSettings Settings for reading email
	 * @return Future that resolves into a success or a failure once the archiving process has completed.
	 *         May also contain a set of non-critical failures
	 */
	def apply(attachmentStoreDirectory: Path, since: Option[Instant] = None, deleteArchivedEmails: Boolean = false)
	         (implicit exc: ExecutionContext, cPool: ConnectionPool, readSettings: ReadSettings) =
	{
		// TODO: Remove test prints
		// Reads email information
		EmailReader.filteredDefaultWithAttachments(attachmentStoreDirectory) { h => since.forall { h.sendTime > _ } }
			.iterateAsync(TargetFolders.all,
				deletionRule = if (deleteArchivedEmails) DeleteProcessed else NeverDelete) { messagesIter =>
				cPool.tryWith { implicit c =>
					processEmails(messagesIter, attachmentStoreDirectory)
				}.flatMapCatching { TryCatch.Success((), _) }
			}
	}
	
	private def processEmails(messagesIterator: Iterator[Try[Email]], attachmentsDirectory: Path)
	                         (implicit connection: Connection) =
	{
		// Queues unresolved message ids associated with existing threads
		val unresolvedThreadIdPerMessageId = mutable.Map[String, Int]()
		// Collects all encountered message ids
		val messageIds = mutable.Map[String, Int]()
		// Collects encountered failures
		val failuresBuilder = new VectorBuilder[Throwable]()
		
		// Processes the initial batch, delaying the processing of messages where reply references can't be resolved
		val skippedEmailsBuilder = new VectorBuilder[Email]()
		messagesIterator.foreach {
			case Success(email) =>
				processEmail(email, messageIds, unresolvedThreadIdPerMessageId, failuresBuilder, attachmentsDirectory,
					skipIfMissingReplyReference = true)
			case Failure(error) => failuresBuilder += error
		}
		val skippedEmails = skippedEmailsBuilder.result()
		
		// Recursively handles the remaining items until reply references are either all resolved or can't be resolved
		// anymore
		if (skippedEmails.nonEmpty) {
			println(s"${skippedEmails.size} emails were delayed because they were missing a reply reference; Processes these next...")
			resolveReplyReferences(skippedEmails, messageIds, unresolvedThreadIdPerMessageId, failuresBuilder,
				attachmentsDirectory)
		}
		
		// Returns the encountered failures
		failuresBuilder.result()
	}
	
	@tailrec
	private def resolveReplyReferences(emails: Vector[Email], messageIds: mutable.Map[String, Int],
	                                   unresolvedThreadIdPerMessageId: mutable.Map[String, Int],
	                                   failuresBuilder: VectorBuilder[Throwable], attachmentsDirectory: Path)
	                                  (implicit connection: Connection): Unit =
	{
		// Processes the next set of emails. Remembers which were skipped.
		val remaining = emails.filterNot {
			processEmail(_, messageIds, unresolvedThreadIdPerMessageId, failuresBuilder, attachmentsDirectory,
				skipIfMissingReplyReference = true)
		}
		// Case: Next iteration is still required
		if (remaining.nonEmpty) {
			// Case: No emails were processed in the last iteration => Won't attempt to fulfill reply references anymore
			if (remaining.size == emails.size) {
				println(s"There were ${remaining.size} emails where reply references couldn't be resolved. Processes these as original messages...")
				remaining.foreach {
					processEmail(_, messageIds, unresolvedThreadIdPerMessageId, failuresBuilder, attachmentsDirectory,
						skipIfMissingReplyReference = false)
				}
			}
			// Case: Some emails were processed in the last iteration => Uses recursion to process the remaining emails
			else
				resolveReplyReferences(remaining, messageIds, unresolvedThreadIdPerMessageId,
					failuresBuilder, attachmentsDirectory)
		}
	}
	
	private def processEmail(email: Email, messageIds: mutable.Map[String, Int],
	                         unresolvedThreadIdPerMessageId: mutable.Map[String, Int],
	                         failuresBuilder: VectorBuilder[Throwable], attachmentsDirectory: Path,
	                         skipIfMissingReplyReference: Boolean)
	                        (implicit connection: Connection) =
	{
		// May skip / delay the processing if a reply reference is missing
		val isReply = email.inReplyTo.nonEmpty
		val replyReferenceId = if (isReply) messageIds.get(email.inReplyTo) else None
		
		// Case: Required reply reference is missing => Returns indicating that processing was skipped
		if (skipIfMissingReplyReference && isReply && replyReferenceId.isEmpty)
			false
		// Case: Allowed to proceed
		else {
			println("\n-----------------------")
			println(s"Processing email from ${email.sender.addressPart} (${
				email.sender.namePart
			}), received at ${email.sendTime.toLocalDateTime}")
			
			// Finds the address ids of those involved
			val allEmailAddresses = email.recipients.all :+ email.sender
			// Inserted & existing address ids, each mapped to their lower case string representation
			val groupedAddressIds = DbAddresses.store(allEmailAddresses.map { _.addressPart }.toSet)
				.map { _.map { a => a.address.toLowerCase -> a.id }.toMap }
			val addressIds = groupedAddressIds.merge { _ ++ _ }
			val senderId = addressIds(email.sender.addressPart.toLowerCase)
			val senderWasInserted = groupedAddressIds.first.exists { _._2 == senderId }
			// Inserts names for the new addresses and assigns names for the existing addresses
			val namePerAddressId = allEmailAddresses
				.flatMap { address =>
					address.namePart.notEmpty
						.map { name => addressIds(address.addressPart.toLowerCase) -> name }
				}
				.toMap
			// Inserted (first) & existing (second) name assignments as:
			// 1) Address id
			// 2) Name to assign
			// 3) Whether name is self-assigned
			val nameAssignments = groupedAddressIds.map {
				_.flatMap { case (_, addressId) =>
					namePerAddressId.get(addressId).map { name => (addressId, name, addressId == senderId) }
				}
			}
			// Names of new addresses are inserted without duplicate-checking
			AddressNameModel.insert(nameAssignments.first.map { case (addressId, name, selfAssigned) =>
				AddressNameData(addressId, name, isSelfAssigned = selfAssigned)
			}.toVector)
			// Names of existing addresses are assigned using more checks
			DbAddressNames.assign(nameAssignments.second.map { case (addressId, name, selfAssigned) =>
				addressId -> Vector(name -> selfAssigned)
			}.toMap)
			
			// Inserts the subject
			// "Re:" etc. initials are removed from the subject before storing it
			val baseSubjectStartIndex = subjectPrefixRegex
				.endIndexIteratorIn(email.subject).nextOption.getOrElse(0)
			// Left if new, right if existing
			val subject = email.subject.drop(baseSubjectStartIndex).notEmpty.map { DbSubject.store(_) }
			subject.foreach { subject =>
				println(s"Processed subject${if (subject.isLeft) " (NEW)" else ""}: ${
					email.subject.drop(baseSubjectStartIndex)}")
			}
			
			val refs = (email.inReplyTo.notEmpty match {
				case Some(parentId) => email.references.appendIfDistinct(parentId)
				case None => email.references
			}).toSet
			// Checks whether the message is associated with any existing thread
			// Method 1: Look to complete an unresolved reference based on message id
			val existingThreadId = email.messageId.notEmpty.flatMap(unresolvedThreadIdPerMessageId.get)
				.orElse {
					NotEmpty(refs).flatMap { refs =>
						// Method 2: Look to complete an unresolved reference based on another reference
						refs.findMap(unresolvedThreadIdPerMessageId.get).orElse {
							// Method 3: Find a thread from any existing reference
							DbMessages.withMessageIds(refs).threadIds.headOption
						}
					}.orElse {
						// Method 4: Find a thread with identical subject
						// involving the message sender
						// Won't perform the search if
						// either the sender or the subject was just inserted
						if (senderWasInserted)
							None
						else
							subject.flatMap {
								_.rightOption.flatMap { subject =>
									DbMessageThread.findIdForPersonalSubject(subject.id, senderId)
								}
							}
					}
				}
			email.messageId.notEmpty.foreach(unresolvedThreadIdPerMessageId.remove)
			println(s"Message id: ${email.messageId}")
			println(s"Existing thread id: $existingThreadId")
			
			// Creates a new thread, if appropriate
			val threadId = existingThreadId.getOrElse { DbMessageThread.newId() }
			// Remembers unresolved thread message ids
			(refs -- messageIds.keySet).foreach { unresolvedThreadIdPerMessageId(_) = threadId }
			
			// Assigns thread subject
			subject.foreach { s => DbMessageThread(threadId).assignSubject(s.either.id) }
			
			// Checks whether this email exists already (compares thread, sender, send time and message id)
			// Left if inserted, right if existed
			val messageId = {
				// If either the sender or the thread was just inserted, won't check for duplicates
				if (senderWasInserted || existingThreadId.isEmpty)
					Left(MessageModel.insert(
						MessageData(threadId, senderId, email.messageId, replyReferenceId, email.sendTime)).id)
				else
					DbMessage(threadId, email.messageId, senderId, email.sendTime).pullOrInsertId(replyReferenceId)
			}
			// Remembers message id
			messageIds(email.messageId) = messageId.either
			// For new messages, writes message contents and assigns attachments
			messageId.leftOption.foreach { messageId =>
				// Removes the HTML content, as well as any reference to an earlier message
				// (unless such message was not found)
				val emailLines = Jsoup.parse(email.message).body().wholeText().linesIterator
					// Removes leading whitespaces and
					// splits to a new line where there are multiple whitespaces in a row
					.flatMap { inputLine =>
						val noSpacesAtBeginning = inputLine.dropWhile { _ == ' ' }
						multiWhiteSpaceRegex.splitIteratorIn(noSpacesAtBeginning)
					}
					.toVector
				val firstReplyLineIndex = replyReferenceId.flatMap { _ =>
					emailLines.indices.find { i =>
						anyReplyLineRegex(emailLines(i)) &&
							((i + 1) to (i + 2))
								.forall { i => emailLines.lift(i).exists { anyReplyLineRegex.apply } }
					}
				}
				val processedEmailText = (firstReplyLineIndex match {
					case Some(i) => emailLines.take(i)
					case None => emailLines
				}).mkString("\n")
					.replaceEachMatchOf(escapedNewLineRegex, "\n")
					.replaceEachMatchOf(manyNewLinesRegex, "\n\n")
				
				// Assigns the message text
				processedEmailText.notEmpty.foreach { text =>
					val statementIds = DbStatements.store(text).map { _.either.id }
					MessageStatementLinkModel
						.insert(statementIds.zipWithIndex.map { case (statementId, index) =>
							MessageStatementLinkData(messageId, statementId, index)
						})
				}
				
				// Assigns email recipients
				MessageRecipientLinkModel.insert(
					email.recipients.map { case (recipient, recipientType) =>
						val addressId = addressIds(recipient.addressPart.toLowerCase)
						MessageRecipientLinkData(messageId, addressId, recipientType)
					}.toVector
				)
				
				// Adds attachments, also
				if (email.attachmentPaths.nonEmpty)
					processAttachments(email.attachmentPaths, messageId, email.sender.addressPart, email
						.sendTime.toLocalDate, attachmentsDirectory, failuresBuilder)
			}
			
			// Returns that this message was processed
			true
		}
	}
	
	private def processAttachments(attachmentPaths: Vector[Path], messageId: Int, senderAddress: String, date: LocalDate,
	                               attachmentsDirectory: Path,
	                               failuresBuilder: VectorBuilder[Throwable])
	                              (implicit connection: Connection) =
	{
		// Moves all attachments to a directory based on the sender email address
		val (addressName, domainName) = senderAddress.splitAtFirst("@")
			.mapSecond { _.untilLast(".") }
			.map(processFileName)
			.toTuple
		val modifiedAttachmentPaths = (attachmentsDirectory / domainName / addressName)
			.createDirectories()
			.flatMap { directory =>
				attachmentPaths.tryMap { p =>
					// Modifies the file name as well
					p.moveAs(directory /
						p.fileNameAndType
							.mapFirst { processFileName(_).startingWith(s"$date-") }
							.mkString("."),
						conflictResolve = Rename)
				}
			}
		val fileNames = modifiedAttachmentPaths match {
			// Case: Attachments moved => Stores new file names
			case Success(paths) => paths.map { _.relativeTo(attachmentsDirectory).either.toJson }
			// Case: Failed to move attachments => Uses original file names
			case Failure(error) =>
				error.printStackTrace()
				failuresBuilder += error
				attachmentPaths.map { _.fileName }
		}
		AttachmentModel.insert(fileNames.map { AttachmentData(messageId, _) })
	}
	
	private def processFileName(fileName: String) =
		validFileNamePartRegex.matchesIteratorFrom(fileName)
			.filter { _.nonEmpty }.mkString("-")
			.replaceEachMatchOf(multiDashRegex, "-")
			.notStartingWith("-").notEndingWith("-")
}
