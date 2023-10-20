package vf.emissary.controller.archive

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Safelist
import utopia.courier.controller.read.{EmailReader, TargetFolders}
import utopia.courier.model.read.ReadSettings
import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.Pair
import utopia.flow.collection.mutable.builder.CompoundingVectorBuilder
import utopia.flow.parse.string.Regex
import utopia.flow.time.Now
import utopia.flow.time.TimeExtensions._
import utopia.flow.util.StringExtensions._
import utopia.flow.util.logging.Logger
import utopia.flow.util.{NotEmpty, UncertainBoolean}
import utopia.vault.database.Connection
import vf.emissary.controller.archive.ArchivingEmailProcessor.DelayedMessageInsert
import vf.emissary.database.access.many.messaging.message.DbMessages
import vf.emissary.database.access.many.messaging.pending_reply_reference.DbPendingReplyReferences
import vf.emissary.database.access.many.messaging.pending_thread_reference.DbPendingThreadReferences
import vf.emissary.database.access.single.messaging.message.DbMessage
import vf.emissary.database.model.messaging._
import vf.emissary.model.partial.messaging._

import java.nio.file.Path
import scala.annotation.tailrec
import scala.collection.immutable.VectorBuilder
import scala.collection.{StringOps, mutable}
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
 * An interface used for storing read email information to the database
 * @author Mikko Hilpinen
 * @since 13.10.2023, v0.1
 */
object ArchiveEmails
{
	// ATTRIBUTES   -----------------------
	
	private val skippedEmailBufferResolveInterval = 30
	
	private lazy val whiteSpace = ' '
	private lazy val manyWhiteSpacesRegex = Regex.whiteSpace.times(3) + Regex.whiteSpace.oneOrMoreTimes
	private lazy val multiWhiteSpaceRegex = Regex.whiteSpace + Regex.whiteSpace.oneOrMoreTimes
	private lazy val manyNewLinesRegex = Regex.newLine.times(3) + Regex.newLine.anyTimes
	private lazy val escapedNewLineRegex = Regex.backslash + Regex("n")
	
	private lazy val htmlTagRegex = Regex.escape('<') +
		(Regex.letterOrDigit || Regex.anyOf(" .-+;#=\"':/!")).withinParenthesis.oneOrMoreTimes +
		Regex.escape('>')
	
	private lazy val zonerReplyLineRegex = Regex.escape('>') + Regex.any
	private lazy val replyHeaderRegex = Regex.letter + Regex.letter.oneOrMoreTimes + Regex.escape(':') +
		Regex.whiteSpace + Regex.any
	private lazy val anyReplyLineRegex = zonerReplyLineRegex.withinParenthesis || replyHeaderRegex.withinParenthesis
	
	// &nbsp; is often found within emails, having no actual function
	private lazy val nbspRegex = Regex.escape('&') + Regex("nbsp") + Regex.escape(';')
	
	
	// OTHER    ---------------------------
	
	/**
	 * Reads and archives emails.
	 * NB: Blocks for extended periods of time!
	 * @param attachmentStoreDirectory Directory where email attachments should be stored
	 * @param continueCondition A condition that must be met for email reading to continue (call-by-name).
	 *                          Default = continue until completed (may take hours).
	 * @param connection Implicit database connection to utilize during the archiving process
	 * @param readSettings             Settings for reading email
	 * @return Encountered failures
	 */
	def apply(attachmentStoreDirectory: Path, continueCondition: => Boolean = true)
	          (implicit connection: Connection, exc: ExecutionContext, readSettings: ReadSettings, log: Logger): Unit =
	{
		// Queues unresolved message ids associated with existing threads
		// Loads initial data-set from the database
		val initialUnresolvedThreadReferences = DbPendingThreadReferences.pull
		val unresolvedThreadIdPerMessageId = mutable.Map[String, Int]()
		unresolvedThreadIdPerMessageId
			.addAll(initialUnresolvedThreadReferences.map { r => r.referencedMessageId -> r.threadId })
		// Collects all encountered message ids
		val messageIds = mutable.Map[String, Int]()
		// Loads the initial data-set from the database
		messageIds.addAll(DbMessages.withMessageId.messageIdMap)
		// Collects encountered failures
		val failuresBuilder = new VectorBuilder[Throwable]()
		
		// Processes the initial batch, delaying the processing of messages where reply references can't be resolved
		val skippedEmailsBuffer = new CompoundingVectorBuilder[DelayedMessageInsert]()
		var unresolvedEmailsCount = 0
		var unresolvedEmailsThreshold = skippedEmailBufferResolveInterval
		var lastMessageTimeCompletionTime = Now.toInstant
		// Continually reads messages as long as there are some
		EmailReader { headers =>
			ArchivingEmailProcessor(headers, messageIds, unresolvedThreadIdPerMessageId, attachmentStoreDirectory)
		}.iterateBlocking(TargetFolders.all) { messagesIterator =>
			messagesIterator.prePollingAsync(3).foreachWhile(continueCondition) {
				// Case: Message successfully read => Attempts to process it
				case Success(delay) =>
					val processTime = Now - lastMessageTimeCompletionTime
					println(s"Message process time: ${processTime.description}")
					delay match {
						// Messages that lack important references are not processed immediately
						// These are delayed for later processing
						case Some(delay) =>
							skippedEmailsBuffer += delay
							unresolvedEmailsCount += 1
							println(s"Current pending completion count is $unresolvedEmailsCount / $unresolvedEmailsThreshold")
						case None =>
							// When the buffer gets large enough, attempts to clear it
							if (unresolvedEmailsCount >= unresolvedEmailsThreshold) {
								println(s"Reached the count of $unresolvedEmailsCount unresolved emails. Starts processing them next.")
								val resolveStartTime = Now.toInstant
								val remainsUnresolved = resolveDelays(skippedEmailsBuffer.popAll(), messageIds)
								unresolvedEmailsCount = remainsUnresolved.size
								skippedEmailsBuffer ++= remainsUnresolved
								// Adjusts the threshold for the next resolve iteration
								unresolvedEmailsThreshold = unresolvedEmailsCount + skippedEmailBufferResolveInterval
								println(s"Resolve process took ${
									(Now - resolveStartTime).description
								}. $unresolvedEmailsCount messages remain unresolved.")
							}
					}
					lastMessageTimeCompletionTime = Now
					
				// Case: Message reading failed => Records the error
				case Failure(error) =>
					println("\nMessage processing failed")
					println(s"Waited for the failed message ${(Now - lastMessageTimeCompletionTime).description}")
					lastMessageTimeCompletionTime = Now
					failuresBuilder += error
			}
		}
		
		// Recursively handles the remaining items until reply references are either all resolved or can't be resolved
		// anymore
		// [(message row id, referenced message id string)]
		val unresolvedReplyReferences = {
			if (skippedEmailsBuffer.nonEmpty) {
				lastMessageTimeCompletionTime = Now
				println(s"${skippedEmailsBuffer.size} emails were delayed because they were missing a reply reference; Processes these next...")
				val remaining = resolveDelays(skippedEmailsBuffer.popAll(), messageIds)
				
				// Forcefully resolves the remaining messages
				remaining.map { delayed => delayed.finalizeInsert() -> delayed.missingMessageId }
			}
			else
				Vector()
		}
		
		// Checks whether some of the previously unresolved reply references may now be resolved
		val previouslyUnresolvedReplyReferences = DbPendingReplyReferences.pull
		val (remainsUnresolvedReplyReference, wasResolvedReplyReference) = previouslyUnresolvedReplyReferences
			.divideBy { reference =>
				messageIds.get(reference.referencedMessageId) match {
					case Some(referencedMessageRowId) =>
						DbMessage(reference.messageId).replyToId = referencedMessageRowId
						true
					case None => false
				}
			}.toTuple
		if (wasResolvedReplyReference.nonEmpty)
			DbPendingReplyReferences(wasResolvedReplyReference.map { _.id }.toSet).delete()
		
		// Documents cases that were left unresolved
		NotEmpty(unresolvedReplyReferences
			.filterNot { case (messageId, reference) =>
				remainsUnresolvedReplyReference.exists { r =>
					r.messageId == messageId && r.referencedMessageId == reference
				}
			})
			.foreach { newUnresolvedReplyReferences =>
				println(s"Could not resolve reply references in ${newUnresolvedReplyReferences.size} emails")
				PendingReplyReferenceModel.insert(
					newUnresolvedReplyReferences.map { case (messageRowId, referencedMessageId) =>
						PendingReplyReferenceData(messageRowId, referencedMessageId)
					}
				)
			}
		
		// Records the final pending thread & reply id status to the database
		val resolvedThreadReferenceIds = initialUnresolvedThreadReferences.view
			.filterNot { r => unresolvedThreadIdPerMessageId.contains(r.referencedMessageId) }
			.map { _.id }.toSet
		if (resolvedThreadReferenceIds.nonEmpty)
			DbPendingThreadReferences(resolvedThreadReferenceIds).delete()
		println(s"${unresolvedThreadIdPerMessageId.size} thread references remain unresolved")
		PendingThreadReferenceModel.insert(unresolvedThreadIdPerMessageId.view
			.filterKeys { messageId => initialUnresolvedThreadReferences.forNone { _.referencedMessageId == messageId } }
			.map { case (messageId, threadId) => PendingThreadReferenceData(threadId, messageId) }
			.toVector)
	}
	
	/**
	 * Processes text, removing all html tags
	 * @param text Text to process
	 * @param skipReplyLines Whether mail reply portion should be removed
	 * @return Processed text in two parts.
	 *         The first part contains lines that are part of the message.
	 *         The second part contains lines that are part of reply messages, but which may be included.
	 *         This second part is always empty if 'skipReplyLines' is set to true.
	 */
	def processText(text: String, senderStrings: => Set[String] = Set(), skipReplyLines: UncertainBoolean = false) = {
		// Removes the HTML content, as well as any reference to an earlier message
		// (unless such message was not found)
		// Jsoup.parse(text).body().wholeText().linesIterator
		// parseHtml(text).linesIterator
		// Extracts the text before the first html tag
		val linesIterator = htmlTagRegex.startIndexIteratorIn(text).nextOption() match {
			case Some(htmlStartIndex) =>
				if (htmlStartIndex == 0)
					parseHtml(text).linesIterator
				else {
					val (nonHtml, html) = text.splitAt(htmlStartIndex)
					(nonHtml.linesIterator :+ "") ++ parseHtml(html).linesIterator
				}
			case None => text.linesIterator
		}
		val emailLines = linesIterator
			// Removes leading whitespaces and
			// splits to a new line where there are multiple whitespaces in a row
			.flatMap { inputLine =>
				val noSpacesAtBeginning = inputLine
					.replace(' ', whiteSpace)
					.replaceEachMatchOf(nbspRegex, "")
					.dropWhile { _ == whiteSpace }
				if (noSpacesAtBeginning.isEmpty)
					Some(noSpacesAtBeginning)
				else
					manyWhiteSpacesRegex.splitIteratorIn(noSpacesAtBeginning)
						.map { _.replaceEachMatchOf(multiWhiteSpaceRegex, " ") }
			}
			.dropWhile { _.isEmpty }
			.takeWhile { s =>
				// Stops if any line starts to contain large numbers of non-standard characters
				s.length < 12 || !(s: StringOps).iterator
					.existsCount(s.length / 4) {
						Character.UnicodeBlock.of(_) != Character.UnicodeBlock.BASIC_LATIN }
			}
			.toVector
			.dropRightWhile { _.isEmpty }
		val (firstReplyLineIndex, authorMentionIndex) = {
			// Case: At least some lines may be skipped (is a thread message)
			if (skipReplyLines.mayBeTrue) {
				val replyStartIndex = emailLines.indices.find { i =>
					anyReplyLineRegex(emailLines(i)) &&
						((i + 1) to (i + 2))
							.forall { i => emailLines.lift(i).exists { s => s.isEmpty || anyReplyLineRegex.apply(s) } }
				}
				// Case: Reply data is not kept => Skips all lines from the first reply line onwards
				if (skipReplyLines.isCertainlyTrue)
					replyStartIndex -> None
				// Case: Reply data is kept =>
				// Still removes those lines that are interpreted to be written by the message author
				else {
					val authorMentionIndex = replyStartIndex.flatMap { replyStartIndex =>
						val targetStrings = senderStrings
						if (targetStrings.nonEmpty) {
							val authorMentionsIter = (replyStartIndex until emailLines.size).iterator.flatMap { i =>
								val line = emailLines(i)
								if (targetStrings.exists { line.contains(_) })
									Some(i)
								else
									None
							}
							// Skips the first mention, as it is usually in a "to" -section
							authorMentionsIter.nextOption().flatMap { _ => authorMentionsIter.nextOption() }
						}
						else
							None
					}
					replyStartIndex -> authorMentionIndex
				}
			}
			// Case: Message is not expected to contain any reply information
			else
				None -> None
		}
		// First group is always accepted, second group is reply-related (potentially accepted)
		val acceptedLines = firstReplyLineIndex match {
			case Some(i) =>
				val primaryLines = emailLines.take(i)
				authorMentionIndex match {
					case Some(i2) => Pair(primaryLines, emailLines.slice(i, i2))
					case None => Pair(primaryLines, Vector())
				}
			case None => Pair(emailLines, Vector())
		}
		
		acceptedLines.map {
			_.mkString("\n")
				.replaceEachMatchOf(escapedNewLineRegex, "\n")
				.replaceEachMatchOf(manyNewLinesRegex, "\n\n")
		}
	}
	
	// Returns the unprocessed emails
	@tailrec
	private def resolveDelays(delays: Vector[DelayedMessageInsert], messageIds: mutable.Map[String, Int])
	                         (implicit connection: Connection): Vector[DelayedMessageInsert] =
	{
		// Processes the next set of emails. Remembers which were skipped.
		val remaining = delays.filter { delayed =>
			if (messageIds.contains(delayed.missingMessageId)) {
				delayed.finalizeInsert()
				false
			}
			else
				true
		}
		// Case: Next iteration is still required
		if (remaining.nonEmpty) {
			// Case: No emails were processed in the last iteration => Won't attempt to fulfill reply references anymore
			if (remaining.size == delays.size)
				remaining
			// Case: Some emails were processed in the last iteration => Uses recursion to process the remaining emails
			else
				resolveDelays(remaining, messageIds)
		}
		else
			remaining
	}
	
	private def parseHtml(html: String) = {
		val jsoupDoc = Jsoup.parse(html)
		val outputSettings = new Document.OutputSettings()
		outputSettings.prettyPrint(false)
		jsoupDoc.outputSettings(outputSettings)
		jsoupDoc.select("br").before("\\n")
		jsoupDoc.select("p").before("\\n")
		val str = jsoupDoc.html().replaceAll("\\\\n", "\n")
		Jsoup.clean(str, "", Safelist.none(), outputSettings)
	}
}
