package vf.emissary.controller.app.command

import utopia.flow.async.AsyncExtensions._
import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.{Empty, Pair, Single}
import utopia.flow.parse.file.FileExtensions._
import utopia.flow.time.TimeExtensions._
import utopia.flow.util.NotEmpty
import utopia.flow.util.StringExtensions._
import utopia.flow.util.result.TryExtensions._
import utopia.flow.util.console.ConsoleExtensions._
import utopia.flow.util.console.{ArgumentSchema, Command}
import utopia.flow.view.immutable.eventful.AlwaysFalse
import utopia.flow.view.mutable.Pointer
import utopia.flow.view.mutable.eventful.{AssignableOnce, SettableFlag}
import utopia.flow.view.template.eventful.Flag
import utopia.vault.database.Connection
import vf.emissary.controller.read.FindMessages
import vf.emissary.database.EmissaryContext
import vf.emissary.database.EmissaryContext._
import vf.emissary.model.combined.messaging.DetailedMessageThread
import vf.emissary.model.stored.messaging.Attachment

import scala.collection.immutable.VectorBuilder
import scala.concurrent.Future
import scala.io.StdIn

/**
 * Provides console commands for searching & reading archived emails
 *
 * @author Mikko Hilpinen
 * @since 24.12.2024, v1.1
 */
object SearchCommands
{
	// ATTRIBUTES   ------------------------------
	
	// Pointers for storing the current state (search result threads, opened thread & next message)
	private val threadQueuePointer = Pointer.eventful.empty[(Iterator[DetailedMessageThread], SettableFlag)]
	private val queuedThreadsPointer = Pointer.eventful.emptySeq[DetailedMessageThread]
	private val openThreadPointer = Pointer.eventful.empty[DetailedMessageThread]
	private val lastAttachmentsPointer = Pointer.eventful.emptySeq[Attachment]
	
	private val nextThreadIndexPointer = Pointer.eventful(0)
	private val nextMessageIndexPointer = Pointer.eventful(0)
	
	private val nextThreadPointer = queuedThreadsPointer.mergeWith(nextThreadIndexPointer) { _.lift(_) }
	private val nextMessagePointer = openThreadPointer.mergeWith(nextMessageIndexPointer) { (thread, messageIndex) =>
		thread.flatMap { _.messages.lift(messageIndex) }
	}
	
	// Pointers that track the current state, making some commands available or disabled
	private val searchingFlag: Flag = queuedThreadsPointer.map { _.nonEmpty }
	private val canQueueMoreFlag: Flag = threadQueuePointer.flatMap {
		case Some((_, closeFlag)) => !closeFlag
		case None => AlwaysFalse
	}
	private val hasNextThreadFlag: Flag = (nextThreadPointer.map { _.isDefined }: Flag) || canQueueMoreFlag
	private val hasNextMessageFlag: Flag = nextMessagePointer.map { _.isDefined }
	private val hasNextFlag = hasNextMessageFlag || hasNextThreadFlag
	private val hasAttachmentsFlag: Flag = lastAttachmentsPointer.map { _.nonEmpty }
	
	private val searchCommand = Command("search", "find",
		help = "Finds specific message threads based on address and word appearance")(
		ArgumentSchema("people", "p",
			help = "Addresses or names that must be involved in the message threads.\nMay be specified as a json array, or separated by \";\". Partial values are allowed"),
		ArgumentSchema("words", "w",
			help = "Words that must appear on the message threads. Partial values are allowed.\nMay be specified as a json array or separated by \";\""),
		ArgumentSchema("other", "o", help =
			"Other words that may appear within the messages. Used in prioritization. Same rules as with 'words'")
	) { args =>
		val addresses = args("people").getVector.view
			.flatMap { _.getString.split(';') }.map { _.trim }.filter { _.nonEmpty }.toSet
		val words = Pair(args("words"), args("other"))
			.map { _.getVector.view.flatMap { _.getString.split(';') }.map { _.trim }.filter { _.nonEmpty }.toSet }
		
		if (addresses.isEmpty && words.forall { _.isEmpty })
			println("Please specify 'people' and/or 'words'")
		else {
			// Performs the search in another thread,
			// because the connection must be kept open for possibly extended time periods
			val immediateResultsPointer = AssignableOnce[(Seq[DetailedMessageThread], Boolean)]()
			Future {
				connectionPool.tryWith { implicit c =>
					// Performs the search
					val (results, closeFlag) = search(addresses, words)
					immediateResultsPointer.set(results -> closeFlag.isDefined)
					
					// Waits until it is safe to close this DB connection
					closeFlag.foreach { _.future.waitFor().log }
				}.failure
					.foreach { error =>
						log(error)
						immediateResultsPointer.trySet(Empty -> false)
					}
			}
			immediateResultsPointer.future.waitFor().log.foreach { case (results, hasMore) =>
				if (results.isEmpty)
					println("No results were found")
				else {
					println(s"Found ${ if (hasMore) ">" else "" }${ results.size } results")
					queuedThreadsPointer.value = results
					listThreads()
				}
			}
		}
	}
	private val filterCommand = Command("filter",
		help = "Further filters the results of the latest message search operation")(
		ArgumentSchema("searched", help = "String that must appear within a message in order for it to be targeted")) {
		args => filterThreads(args("searched").getString)
	}
	private val clearCommand = Command
		.withoutArguments("clear", help = "Clears the currently open search results") { closeOpenSearch() }
	
	private val nextCommand = Command("next", help = "Moves to the next message, or to the next message thread")(
		ArgumentSchema("target", help = "message | thread - If left empty, primarily targets the next message")) {
		args =>
			args("target").getString.headOption match {
				case None =>
					if (hasNextMessage)
						nextMessage()
					else if (hasNextThread)
						nextThread()
					else
						println("No more messages or threads have been queued")
						
				case Some('t') =>
					if (hasNextThread)
						nextThread()
					else
						println("No more threads have been queued")
				
				case Some('m') =>
					if (hasNextMessage)
						nextMessage()
					else
						println("No more messages have been queued")
					
				case other => println(s"Unrecognized target \"$other\"")
			}
	}
	
	private val listCommand = Command("list", "ls", help = "Lists the currently available search results")(
		ArgumentSchema("next",
			help = "The number of new search results to look for. Setting this parameter to >0 removes previously queued results from the list"),
		ArgumentSchema("more",
			help = "Number of additional search results to include besides the already queued results")) {
		args =>
			val nextCount = args("next").getInt max 0
			listThreads(nextCount + (args("more").getInt max 0), skipPreviouslyQueued = nextCount > 0)
	}
	
	/**
	 * A command for opening attachments from the last viewed email.
	 * Must not be used if EmissaryContext.attachmentDirectory is not defined.
	 */
	private val openAttachmentsCommand = Command.withoutArguments("attachments", "files",
		help = "Opens the attachments from the last message") { openAttachments() }
	
	/**
	 * A pointer that contains the search-related commands available at each time
	 */
	val pointer = searchingFlag.mergeWith(hasNextFlag, hasAttachmentsFlag) { (searching, hasNext, hasAttachments) =>
		val commandsBuilder = new VectorBuilder[Command]()
		if (hasAttachments && EmissaryContext.attachmentsEnabled)
			commandsBuilder += openAttachmentsCommand
		if (searching) {
			if (hasNext)
				commandsBuilder += nextCommand
			commandsBuilder += listCommand
			commandsBuilder += filterCommand
			commandsBuilder += clearCommand
		}
		commandsBuilder += searchCommand
		commandsBuilder.result()
	}
	
	
	// INITIAL CODE ------------------------------
	
	// Makes sure the 'nextThreadIndexPointer' points to a valid value
	queuedThreadsPointer.addContinuousListener { e =>
		val commonIndicesCount = e.oldValue.zip(e.newValue)
			.view.takeWhile { case (oldThread, newThread) => oldThread.id == newThread.id }.size
		nextThreadIndexPointer.update { _ max commonIndicesCount }
	}
	
	// Makes sure 'nextMessageIndexPointer' remains valid
	openThreadPointer.addContinuousListener { _ => nextMessageIndexPointer.value = 0 }
	
	
	// COMPUTED --------------------------------
	
	private def hasNextThread = nextThreadPointer.value.isDefined || queueMoreThreads(1)
	private def hasNextMessage = nextMessagePointer.value.isDefined
	
	
	// OTHER    --------------------------------
	
	/**
	 * Closes any open search, releasing resources associated with it
	 */
	def closeOpenSearch() = {
		// Marks the search results as closeable
		threadQueuePointer.pop().foreach { _._2.set() }
		
		// Clears cached data
		queuedThreadsPointer.clear()
		openThreadPointer.clear()
		lastAttachmentsPointer.clear()
	}
	
	/**
	 * Performs a new message search
	 * @param people Searched people
	 * @param words Searched words, where first values are required and second values additional
	 *              and used for prioritization
	 * @param connection Implicit DB connection
	 * @return Returns 2 values:
	 *              1. Immediate search results (0-5 message threads)
	 *              1. A flag that is set to true once all search results have been loaded.
	 *                 None if all search results were already loaded.
	 */
	private def search(people: Set[String], words: Pair[Set[String]])(implicit connection: Connection) = {
		// Closes the previous search
		closeOpenSearch()
		
		if (people.isEmpty && words.forall { _.isEmpty }) {
			println("No search targets were specified")
			Empty -> None
		}
		else {
			// Describes the search
			println("\nFinding message threads that: ")
			NotEmpty(people).foreach { people =>
				people.oneOrMany match {
					case Left(only) => println(s"\t- Involve $only")
					case Right(people) => println(s"\t- Involve at least one person from: ${people.mkString(", ")}")
				}
			}
			NotEmpty(words.first).foreach { words =>
				words.oneOrMany match {
					case Left(only) => println(s"\t- Contain word \"$only\"")
					case Right(words) => println(s"\t- Contain any of: ${ words.view.map { _.quoted }.mkString(", ") }")
				}
			}
			NotEmpty(words.second).foreach { words =>
				println(s"Prioritizes messages including: ${ words.view.map { _.quoted }.mkString(", ") }")
			}
			
			// Starts the search
			val threadQueue = FindMessages(people, words.first, words.second)
			// Case: Found at least 1 result => Immediately loads 5 results
			if (threadQueue.hasNext) {
				println("\nLoading results...")
				val loadedThreads = threadQueue.collectNext(5)
				
				// Case: Has more results => Remembers the queue so that those may be pulled as needed
				if (threadQueue.hasNext)
					threadQueuePointer.value = Some(threadQueue -> SettableFlag())
				// Case: No more results are available => No need to keep the queue open
				else
					threadQueuePointer.clear()
					
				loadedThreads -> threadQueuePointer.value.map { _._2 }
			}
			// Case: No results were found
			else {
				threadQueuePointer.clear()
				Empty -> None
			}
		}
	}
	
	private def filterThreads(targetStr: String) = {
		val originalThreads = queuedThreadsPointer.value
		val filtered = originalThreads
			.filter { _.messages.exists { _.statements.exists { _.toString.contains(targetStr) } } }
			.notEmpty
			// If the filtering didn't yield any results, may continue the original search a little bit
			.getOrElse {
				threadQueuePointer.value match {
					case Some((queue, closeFlag)) =>
						println("Searches for additional message threads...")
						val queued = queue.take(8).caching
						val results = queued
							.find { _.messages.exists { _.statements.exists { _.toString.contains(targetStr) } } } match
						{
							// Case: Found a match => That will be the only search result
							case Some(foundResult) => Single(foundResult)
							// Case: Didn't find a match => Includes the loaded message threads in the queued threads
							case None =>
								queuedThreadsPointer.update { _ ++ queued.current }
								Empty
						}
						// Closes the search results, if appropriate
						if (!queue.hasNext)
							closeFlag.set()
							
						results
						
					case None => Empty
				}
			}
		
		// Case: No results => No change
		if (filtered.isEmpty)
			println(s"No messages contained \"$targetStr\"")
		// Case: Filtering successful => Updates the list of threads
		else {
			queuedThreadsPointer.value = filtered
			nextThreadIndexPointer.value = 0
			openThreadPointer.filterCurrent { t => filtered.exists { _.id == t.id } }
			
			// Lists the results afterwards
			listThreads()
		}
	}
	
	private def listThreads(additionalResultsCount: Int = 0, skipPreviouslyQueued: Boolean = false) = {
		// Prepares the targeted threads
		val threads = {
			// Case: Only targeting new threads
			if (skipPreviouslyQueued) {
				val previousThreadCount = queuedThreadsPointer.value.size
				if (queueMoreThreads(additionalResultsCount))
					queuedThreadsPointer.value.drop(previousThreadCount)
				else
					Empty
			}
			// Case: Targeting existing and/or new threads => May still limit the amount of displayed threads
			else {
				queueMoreThreads(additionalResultsCount)
				queuedThreadsPointer.value.takeRight(20)
			}
		}
		// Case: No threads found
		if (threads.isEmpty)
			println(s"No ${ if (skipPreviouslyQueued) "more " else "" }message threads are available")
		// Case: Threads found => Allows the user to select from them
		else
			StdIn
				.selectFrom(threads.map { t =>
					val subjectStr = t.subject match {
						case Some(subject) => subject.toString
						case None => "No subject"
					}
					val timeStr = t.lastMessageSendTime match {
						case Some(t) => t.toLocalDate.toString
						case None => ""
					}
					val peopleStr = {
						if (t.involvedAddresses.hasSize <= 2)
							t.involvedAddresses.toOptimizedSeq.sorted.mkString(" and ")
						else
							s"${ t.involvedAddresses.size } people"
					}
					t -> s"$subjectStr${ timeStr.prependIfNotEmpty(" -") }: ${
						t.messages.size } messages between $peopleStr"
				}, "threads", "open")
				.foreach { thread =>
					// Sets the next thread index correctly first
					queuedThreadsPointer.value.findIndexWhere { _.id == thread.id }
						.foreach { currentIndex => nextThreadIndexPointer.value = currentIndex + 1 }
					
					// Opens the selected thread
					open(thread)
				}
	}
	
	private def nextThread() = {
		// Finds the next thread to target
		nextThreadPointer.value
			// If necessary, attempts to pull more search results
			.orElse { if (queueMoreThreads(1)) nextThreadPointer.value else None }
			.foreach { thread =>
				// Advances the next thread index
				nextThreadIndexPointer.update { _ + 1 }
				// Opens the next thread
				open(thread)
			}
	}
	
	/**
	 * Pulls n more threads from the search results
	 * @param n The number of threads to pull
	 * @return Whether new threads were pulled
	 */
	private def queueMoreThreads(n: Int) = {
		if (n > 0)
			threadQueuePointer.value match {
				case Some((queue, closeFlag)) =>
					// Pulls the threads
					val newThreads = queue.collectNext(n)
					queuedThreadsPointer.update { _ ++ newThreads }
					
					// If there are no more threads available, closes the queue
					if (!queue.hasNext)
						closeFlag.set()
					
					newThreads.nonEmpty
				
				// Case: No queue is open
				case None => false
			}
		// Case: Not pulling any threads
		else
			false
	}
	
	private def open(thread: DetailedMessageThread) = {
		// Marks this thread as opened
		openThreadPointer.setOne(thread)
		
		// Prints information about this thread
		println("\n-----------------------------")
		NotEmpty(thread.subjects) match {
			case Some(subjects) => println(s"Thread #${ thread.id }: ${ subjects.mkString(" / ") }")
			case None => println(s"Thread #${thread.id} (no subject)")
		}
		println("-----------------------------")
		if (thread.messages.nonEmpty)
			println(thread.messages.ends.map { _.created.toLocalDate }.distinct.mkString(" - "))
		val addresses = thread.involvedAddresses
		println(s"Involves ${addresses.size} people:")
		addresses.groupBy { _.domain }.foreach { case (domain, addresses) =>
			println(s"\t- $domain: ${
				addresses.map { a => a.name.getOrElse { a.address.address.untilFirst("@") } }.mkString(", ")
			}")
		}
		println(s"${thread.messages.size} messages")
		
		// Prints instructions
		if (thread.messages.nonEmpty) {
			println("\nIn order to read the messages in this thread, please use the \"next\" command")
			if (hasNextThread)
				println("If you want to skip to the next thread, please use the \"next thread\" command")
		}
		else if (hasNextThread)
			println(s"In order to move to the next thread, please use the \"next\" command")
	}
	
	private def nextMessage() = {
		nextMessagePointer.value match {
			case Some(message) =>
				// Advances the next message index
				nextMessageIndexPointer.update { _ + 1 }
				
				// Prints the message
				println("\n--------------------------\n")
				printInParts(message.toString)
				lastAttachmentsPointer.value = message.attachments
				
				// Prints instructions
				if (hasNextMessage) {
					println("\nIn order to print the next message, please use the \"next\" command")
					if (hasNextThread)
						println(s"If you want to skip to the next thread, use the \"next thread\" command")
				}
				else if (hasNextThread)
					println("\nThis was the last message in this thread.\nPlease use the \"next\" command to move to the next thread")
				else
					println("\nThis is the last message in this thread.")
			
			case None => println("No more messages have been queued")
		}
	}
	
	private def openAttachments() = lastAttachmentsPointer.value.notEmpty.foreach { attachments =>
		attachments.oneOrMany match {
			case Left(attachment) => attachment.path.openInDesktop().log
			case Right(attachments) => attachments.groupBy { _.path.parent }.keys.foreach { _.openDirectory().log }
		}
	}
	
	private def printInParts(message: String) = {
		val linesIter = message.linesIterator.map { line => line -> (line.length / 80 + 1) }
		var continues = true
		while (continues && linesIter.hasNext) {
			var printed = 0
			var printMore = true
			while (printMore && linesIter.hasNext) {
				val (line, linesCount) = linesIter.next()
				printed += linesCount
				println(line)
				
				if (line.isEmpty)
					printMore = printed < 10
				else
					printMore = printed < 20
			}
			
			if (linesIter.hasNext) {
				println("\nPress enter to read further. Type \"s\" or \"skip\" to print the rest of the message")
				val input = StdIn.readLine().trim.toLowerCase
				continues = input != "s" && input != "skip"
			}
			else
				continues = false
		}
		linesIter.foreach(println)
	}
}
