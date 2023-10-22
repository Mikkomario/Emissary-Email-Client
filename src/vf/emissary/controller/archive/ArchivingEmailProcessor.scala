package vf.emissary.controller.archive

import utopia.courier.controller.read.{FromEmailBuilder, LazyEmailHeadersView}
import utopia.courier.model.write.Recipients
import utopia.flow.parse.string.{Regex, StringFrom}
import utopia.flow.collection.CollectionExtensions._
import utopia.flow.parse.file.FileUtils
import utopia.flow.parse.file.FileExtensions._
import utopia.flow.time.TimeExtensions._
import utopia.flow.util.{NotEmpty, UncertainBoolean}
import utopia.flow.util.StringExtensions._
import utopia.flow.util.logging.Logger
import utopia.flow.view.immutable.View
import utopia.flow.view.immutable.caching.Lazy
import utopia.flow.view.mutable.eventful.Flag
import utopia.vault.database.Connection
import vf.emissary.controller.archive.ArchivingEmailProcessor.{DelayedMessageInsert, possibleCodecs}
import vf.emissary.database.access.many.messaging.address.DbAddresses
import vf.emissary.database.access.many.messaging.address_name.DbAddressNames
import vf.emissary.database.access.many.messaging.attachment.DbAttachments
import vf.emissary.database.access.many.messaging.message.DbMessages
import vf.emissary.database.access.many.text.statement.DbStatements
import vf.emissary.database.access.single.messaging.message.DbMessage
import vf.emissary.database.access.single.messaging.message_thread.DbMessageThread
import vf.emissary.database.access.single.messaging.subject.DbSubject
import vf.emissary.database.model.messaging.{AddressNameModel, AttachmentModel, MessageModel, MessageRecipientLinkModel, MessageStatementLinkModel}
import vf.emissary.model.partial.messaging.{AddressNameData, AttachmentData, MessageData, MessageRecipientLinkData, MessageStatementLinkData}

import java.io.InputStream
import java.nio.file.Path
import java.time.Instant
import scala.collection.immutable.VectorBuilder
import scala.collection.{StringOps, mutable}
import scala.io.Codec
import scala.util.{Failure, Success, Try}

object ArchivingEmailProcessor
{
	// ATTRIBUTES   ------------------------
	
	private val possibleCodecs = Vector(Codec.UTF8, Codec.ISO8859)
	
	private lazy val subjectPrefixRegex = Regex.startOfLine +
		(Regex.upperCaseLetter + Regex.letter + Regex.escape(':') + Regex.whiteSpace)
			.withinParenthesis.oneOrMoreTimes
	
	
	// OTHER    ----------------------------
	
	/**
	 * Creates a new email processor that archives incoming emails
	 * @param headers Email headers (view)
	 * @param messageIds Mutable map that contains a message row id for each encountered message
	 * @param unresolvedThreadIdPerMessageId Mutable map that contains a thread id for each unresolved message reference
	 * @param attachmentsDirectory Directory where attachments shall be stored
	 * @param connection Implicit DB connection
	 * @param log Logger that receives non-critical failures
	 * @return A new email processor. None if no further email processing is necessary.
	 */
	def apply(headers: LazyEmailHeadersView, deletionFlag: Option[Flag], messageIds: mutable.Map[String, Int],
	          unresolvedThreadIdPerMessageId: mutable.Map[String, Int], attachmentsDirectory: Path,
	          deleteNotAllowedAfter: Instant)
	         (implicit connection: Connection, log: Logger) =
	{
		// May delay the processing if a reply reference is missing
		val inReplyTo = headers.inReplyTo
		val isReply = inReplyTo.nonEmpty
		val replyReferenceId = if (isReply) messageIds.get(inReplyTo) else None
		
		println("\n-----------------------")
		println(s"Processing a ${ if (isReply) "reply" else "message" } from ${headers.sender.addressPart} (${
			headers.sender.namePart
		}), received at ${headers.sendTime.toLocalDateTime}")
		println(s"Message id: ${headers.messageId}")
		
		// Finds the address ids of those involved
		val allEmailAddresses = headers.recipients.all :+ headers.sender
		// Inserted & existing address ids, each mapped to their lower case string representation
		val groupedAddressIds = DbAddresses.store(allEmailAddresses.map { _.addressPart }.toSet)
			.map { _.map { a => a.address.toLowerCase -> a.id }.toMap }
		val addressIds = groupedAddressIds.merge { _ ++ _ }
		val senderId = addressIds(headers.sender.addressPart.toLowerCase)
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
		AddressNameModel.insert(
			nameAssignments.first.map { case (addressId, name, selfAssigned) =>
				AddressNameData(addressId, name, isSelfAssigned = selfAssigned)
			}.toVector
		)
		// Names of existing addresses are assigned using more checks
		DbAddressNames.assign(nameAssignments.second.map { case (addressId, name, selfAssigned) =>
			addressId -> Vector(name -> selfAssigned)
		}.toMap)
		
		// "Re:" etc. initials are removed from the subject before storing it
		val baseSubjectStartIndex = subjectPrefixRegex
			.endIndexIteratorIn(headers.subject).nextOption().getOrElse(0)
		val processedSubjectText = headers.subject.drop(baseSubjectStartIndex)
		println(s"Subject: $processedSubjectText")
		
		val refs = (headers.inReplyTo.notEmpty match {
			case Some(parentId) => headers.references.appendIfDistinct(parentId)
			case None => headers.references
		}).toSet
		
		val lazySenderMatchStrings = Lazy { Set(headers.sender.addressPart) ++ namePerAddressId.get(senderId) }
		
		val replyReferenceIdView = {
			if (replyReferenceId.isDefined)
				View.fixed(replyReferenceId)
			else if (isReply)
				View { messageIds.get(inReplyTo) }
			else
				View.fixed(None)
		}
		val missingReplyReferenceView = {
			if (replyReferenceId.isDefined || !isReply)
				View.fixed("")
			else
				View { if (messageIds.contains(inReplyTo)) "" else inReplyTo }
		}
		def processMessage() = process(headers.messageId, senderId, headers.recipients, processedSubjectText,
			headers.sendTime, refs, replyReferenceIdView.value, addressIds, messageIds, unresolvedThreadIdPerMessageId,
			senderWasInserted)
		
		// Case: Required reply reference is missing => Doesn't immediately process/insert the message,
		// but waits whether the reference may be resolved
		if (isReply && replyReferenceId.isEmpty) {
			// Checks whether the message already exists in the database
			// Case: Message already exists => Won't process message data
			if (DbMessage.matching(headers.messageId, senderId, headers.sendTime).nonEmpty) {
				// Deletes the message, if appropriate
				// WET WET
				if (headers.sendTime < deleteNotAllowedAfter)
					deletionFlag.foreach { deletionFlag =>
						println("Deletes the email instead of processing it, as it had already been read earlier.")
						deletionFlag.set()
					}
				println("Skips the processing since the message has already been read earlier")
				None
			}
			// Case: No such message exists yet => Processes the message, but with a delay
			else {
				val lazyMessageRowId = Lazy { processMessage() }
				Some(new ArchivingEmailProcessor(headers.sender.addressPart, headers.sendTime, missingReplyReferenceView,
					lazyMessageRowId, lazySenderMatchStrings, attachmentsDirectory, deletionFlag, deleteNotAllowedAfter,
					isReply))
			}
		}
		// Case: Message may be immediately inserted
		else {
			val (messageRowId, alreadyExisted) = processMessage()
			// Case: No insert was necessary => Skips email processing
			if (alreadyExisted) {
				// Deletes the message, if appropriate
				if (headers.sendTime < deleteNotAllowedAfter)
					deletionFlag.foreach { deletionFlag =>
						println("Deletes the email instead of processing it, as it had already been read earlier.")
						deletionFlag.set()
					}
				println("Skips the processing since the message has already been read earlier")
				None
			}
			// Case: Inserted a new message => Processes email contents afterwards
			else
				Some(new ArchivingEmailProcessor(headers.sender.addressPart, headers.sendTime, missingReplyReferenceView,
					Lazy.initialized(messageRowId -> alreadyExisted), lazySenderMatchStrings, attachmentsDirectory,
					deletionFlag, deleteNotAllowedAfter, isReply))
		}
	}
	
	private def process(messageId: String, senderId: Int, recipients: Recipients, subjectText: String, sendTime: Instant,
	                    references: Set[String], replyReferenceId: Option[Int], addressIds: Map[String, Int],
	                    messageIds: mutable.Map[String, Int],
	                    unresolvedThreadIdPerMessageId: mutable.Map[String, Int], senderWasInserted: Boolean)
	                   (implicit connection: Connection) =
	{
		// Inserts the subject
		// Left if new, right if existing
		val subject = subjectText.notEmpty.map { DbSubject.store(_) }
		
		// Checks whether the message is associated with any existing thread
		// Method 1: Look to complete an unresolved reference based on message id
		val existingThreadId = messageId.notEmpty.flatMap(unresolvedThreadIdPerMessageId.get)
			.orElse {
				NotEmpty(references).flatMap { refs =>
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
		messageId.notEmpty.foreach(unresolvedThreadIdPerMessageId.remove)
		
		// Creates a new thread, if appropriate
		val threadId = existingThreadId.getOrElse { DbMessageThread.newId() }
		// Remembers unresolved thread message ids
		(references -- messageIds.keySet).foreach { unresolvedThreadIdPerMessageId(_) = threadId }
		
		// Assigns thread subject
		subject.foreach { s => DbMessageThread(threadId).assignSubject(s.either.id) }
		
		// Checks whether this email exists already (compares thread, sender, send time and message id)
		// Left if inserted, right if existed
		val groupedMessageId = {
			// If either the sender or the thread was just inserted, won't check for duplicates
			if (senderWasInserted || existingThreadId.isEmpty)
				Left(MessageModel.insert(
					MessageData(threadId, senderId, messageId, replyReferenceId, sendTime)).id)
			else
				DbMessage(threadId, messageId, senderId, sendTime).pullOrInsertId(replyReferenceId)
		}
		val messageRowId = groupedMessageId.either
		// Remembers message id
		messageIds(messageId) = messageRowId
		// For new messages, assigns email recipients
		groupedMessageId.leftOption.foreach { messageId =>
			MessageRecipientLinkModel.insert(
				recipients.map { case (recipient, recipientType) =>
					val addressId = addressIds(recipient.addressPart.toLowerCase)
					MessageRecipientLinkData(messageId, addressId, recipientType)
				}.toVector
			)
		}
		
		if (groupedMessageId.isRight)
			println("Message already existed in the database")
		else if (existingThreadId.isDefined)
			println("Message was inserted to an existing thread")
		else
			println("New thread and message inserted")
		
		// Returns the message row id and whether the message already existed in the database
		messageRowId -> groupedMessageId.isRight
	}
	
	
	// NESTED   -------------------------
	
	/**
	 * A wrapper class for performing message insert later
	 * @param missingMessageId Reference to a message that should be inserted first, if possible
	 * @param messageSendTime Time when the associated message was sent
	 * @param finalizationFunction Function to call when this message may or must be inserted
	 */
	class DelayedMessageInsert(val missingMessageId: String, val messageSendTime: Instant,
	                           finalizationFunction: () => Int)
	{
		/**
		 * Performs the delayed insert.
		 * If possible, this should be called once the missing message id has been found
		 * and the associated message inserted.
		 * @return Id of the stored message
		 */
		def finalizeInsert() = finalizationFunction()
	}
}

/**
 * An email processor that archives read messages
 * @author Mikko Hilpinen
 * @since 20.10.2023, v0.1
 * @param senderAddress Email sender's email address as a String
 * @param messageSendTime Time when the email was sent
 * @param missingReplyReferenceView A view that contains the message id of the missing inReplyTo -message.
 *                                  Should contain an empty string once the missing reference has been resolved.
 * @param lazyMessageRowId Lazily initialized message row id entry in the database +
 *                         a boolean indicating whether that message **already existed** in the database.
 *                         This container should be pre-initialized if possible.
 *                         It will be initialized from this side as late as possible,
 *                         assuming that the initialization process is more accurate, the later it is performed.
 *                         Assumes that a message and possibly a message thread may be inserted during this process.
 * @param lazySenderStrings A lazily initialized set of strings based on the email sender's address and possibly
 *                          gathered name.
 *                          Used in reply line filtering under certain circumstances.
 *                          May not get called at all.
 * @param attachmentsRootDirectory The directory under which all attachments are stored
 * @param isReply Whether this message is to be considered a reply or not. I.e. whether it makes any references.
 * @param connection Implicit database connection to use. Should be open until the process has been finalized.
 * @param log Logging implementation that receives non-critical failures
 */
class ArchivingEmailProcessor(senderAddress: String, messageSendTime: Instant, missingReplyReferenceView: View[String],
                              lazyMessageRowId: Lazy[(Int, Boolean)],
                              lazySenderStrings: Lazy[Set[String]],
                              attachmentsRootDirectory: Path, deletionFlag: Option[Flag],
                              deleteNotAllowedAfter: Instant, isReply: Boolean)
                             (implicit connection: Connection, log: Logger)
	extends FromEmailBuilder[Option[DelayedMessageInsert]]
{
	// ATTRIBUTES   --------------------------
	
	private var processedMessage = ""
	private var possibleReplyRemainder = ""
	
	// Set to true if any failure is encountered
	// (prevents message deletion)
	private var hasFailed = false
	
	private val attachmentPathsBuilder = new VectorBuilder[Path]()
	
	// Moves all attachments to a directory based on the sender email address
	 private val lazyAttachmentsDirectory = Lazy {
		 val (addressName, domainName) = senderAddress.splitAtFirst("@")
			 .mapSecond { _.untilLast(".") }
			 .map { s => FileUtils.normalizeFileName(s.replace('.', '-')) }
			 .toTuple
		 (attachmentsRootDirectory/domainName/addressName).createDirectories()
	 }
	
	
	// COMPUTED ------------------------------
	
	// May only skip message processing if it is already known that the message already exists in the database
	private def maySkipContentProcessing = lazyMessageRowId.current.exists { _._2 }
	
	
	// IMPLEMENTED  --------------------------
	
	// TODO: Possibly add message overwrite mode
	override def append(message: String): Try[Unit] = {
		if (maySkipContentProcessing)
			println("Skipping email message processing")
		else {
			println("Processing message text content...")
			// Removes html from email body
			// First element is main message part, second element is possible reply part
			val shouldSkipReplyLines: UncertainBoolean = {
				if (isReply) {
					if (missingReplyReferenceView.value.isEmpty) true else UncertainBoolean
				}
				else
					false
			}
			val processedEmailText = ArchiveEmails.processText(message, lazySenderStrings.value,
				skipReplyLines = shouldSkipReplyLines)
			
			// Checks whether the received text is similar to something that was read during another method call
			// Case: There already exists a stored message or message part
			if (processedMessage.nonEmpty) {
				val areSimilar = (processedMessage: StringOps).iterator.filter { _.isLetter }
					.zip((processedEmailText.first: StringOps).iterator.filter { _.isLetter })
					.take(96).forall { case (existing, proposed) => existing == proposed }
				// Case: The two read parts are distinctly different => Stores them back to back
				if (!areSimilar) {
					processedMessage = s"$processedMessage\n\n${processedEmailText.first}"
					if (processedEmailText.second.nonEmpty) {
						if (possibleReplyRemainder.nonEmpty)
							possibleReplyRemainder = s"$possibleReplyRemainder\n\n${processedEmailText.second}"
					}
				}
			}
			// Case: No message has been read yet => Remembers this message
			else {
				processedMessage = processedEmailText.first
				possibleReplyRemainder = processedEmailText.second
			}
		}
		
		Success(())
	}
	override def appendFrom(stream: InputStream): Try[Unit] = {
		// Case: Content-processing is not required => Skips stream-processing
		if (maySkipContentProcessing) {
			println("Skips email content processing")
			Success(())
		}
		// Case: Content-processing is necessary => Parses the stream, using multiple encoding options, if necessary
		else {
			println("Parsing message content stream...")
			val attemptResults = possibleCodecs.iterator
				.map { implicit codec => StringFrom.stream(stream) }
				.collectTo { _.isSuccess }
			// Fails with the original encoding option, if applicable
			attemptResults.last.orElse { attemptResults.head }
				// Appends content from the read stream
				.flatMap(append)
		}
	}
	
	override def attachFrom(attachmentName: String, stream: InputStream): Try[Unit] = {
		// Uses a normalized name, combined with the message send date
		val modifiedAttachmentName = attachmentName.splitAtLast(".")
			.mapSecond { _.takeWhile { _.isLetterOrDigit } }
			.map(FileUtils.normalizeFileName)
			.merge { (namePart, extensionPart) =>
				s"$namePart.${ extensionPart.nonEmptyOrElse("txt") }"
					.startingWith(s"${messageSendTime.toLocalDate}-")
			}
		
		// Checks whether the specified file already exists
		// Only reads content if not
		// Also skips writing if the attachments directory couldn't be written
		lazyAttachmentsDirectory.value.foreach { directory =>
			val storePath = directory/modifiedAttachmentName
			// Case: File already exists => Skips reading and registers a connection to that file instead
			if (storePath.exists) {
				println(s"Attachment $storePath already existed on the disk")
				attachmentPathsBuilder += storePath
			}
			// Case: File doesn't exists => Attempts to write the file based on streamed content
			else {
				println(s"Saving attachment to $storePath...")
				storePath.writeStream(stream) match {
					case Success(filePath) => attachmentPathsBuilder += filePath
					case Failure(error) =>
						hasFailed = true
						log(error, s"Failed to write file $storePath")
				}
			}
		}
		
		// Will always return a success, even when file parsing fails (so as to not interrupt the message processing)
		Success(())
	}
	
	override def result(): Try[Option[DelayedMessageInsert]] = {
		// Records a failure if attachments directory couldn't be created
		lazyAttachmentsDirectory.current.flatMap { _.failure }
			.foreach { error =>
				hasFailed = true
				log(error, "Failed to create the attachments directory")
			}
		// May delay the message finalization, if there's a missing reply reference
		val result = missingReplyReferenceView.value.notEmpty match {
			case Some(missingReference) =>
				println("Message completion is delayed")
				Some(new DelayedMessageInsert(missingReference, messageSendTime, finalizeProcess))
			case None =>
				finalizeProcess()
				None
		}
		Success(result)
	}
	
	
	// OTHER    -------------------------
	
	// Inserts the collected data
	private def finalizeProcess(): Int = {
		// Inserts the message, if not inserted already
		val (messageRowId, alreadyExisted) = lazyMessageRowId.value
		
		// Assigns the message text, if appropriate
		if (!alreadyExisted && processedMessage.nonEmpty) {
			// If the reply reference was not resolved, may insert a longer text
			val textToInsert = {
				if (possibleReplyRemainder.nonEmpty && missingReplyReferenceView.value.nonEmpty)
					s"$processedMessage\n\n$possibleReplyRemainder"
				else
					processedMessage
			}
			val statementIds = DbStatements.store(textToInsert).map { _.either.id }
			MessageStatementLinkModel
				.insert(statementIds.zipWithIndex.map { case (statementId, index) =>
					MessageStatementLinkData(messageRowId, statementId, index)
				})
		}
		
		// Records attachment links
		val relativePaths = {
			val relativePaths = attachmentPathsBuilder.result()
				.map { _.relativeTo(attachmentsRootDirectory).either.toJson }
			// May check for duplicates, if there is a possibility for those
			if (alreadyExisted && relativePaths.nonEmpty) {
				val existingPaths = DbAttachments.inMessage(messageRowId).fileNames.toSet
				// Won't store duplicate entries
				relativePaths.filterNot(existingPaths.contains)
			}
			else
				relativePaths
		}
		AttachmentModel.insert(relativePaths.map { AttachmentData(messageRowId, _) })
		println("Message fully processed")
		
		// May delete the original message, but not if any reading process failed
		if (!hasFailed && messageSendTime < deleteNotAllowedAfter)
			deletionFlag.foreach { deletionFlag =>
				println("Deletes the original message")
				deletionFlag.set()
			}
		
		messageRowId
	}
}
