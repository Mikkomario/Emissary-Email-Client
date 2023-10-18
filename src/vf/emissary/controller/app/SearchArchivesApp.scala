package vf.emissary.controller.app

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.Pair
import utopia.flow.time.TimeExtensions._
import utopia.flow.util.NotEmpty
import utopia.flow.util.StringExtensions._
import utopia.flow.util.console.{ArgumentSchema, Command, Console}
import vf.emissary.controller.read.FindMessages
import vf.emissary.model.combined.messaging.{DetailedMessage, DetailedMessageThread}
import vf.emissary.util.Common._

/**
 * A console application used for searching the email archives
 * @author Mikko Hilpinen
 * @since 15.10.2023, v0.1
 */
object SearchArchivesApp extends App
{
	// ATTRIBUTES   -------------------------
	
	private var threadQueue = Iterator.empty[DetailedMessageThread]
	private var messagesQueue = Iterator.empty[DetailedMessage]
	
	private def open(thread: DetailedMessageThread) = {
		// Queues the messages
		messagesQueue = thread.messages.iterator
		
		// Prints information about this thread
		println()
		NotEmpty(thread.subjects) match {
			case Some(subjects) => println(subjects.mkString(" / "))
			case None => println(s"Thread #${thread.id} (no subject)")
		}
		if (thread.messages.nonEmpty)
			println(thread.messages.ends.map { _.created.toLocalDate }.distinct.mkString(" - "))
		val addresses = thread.involvedAddresses
		println(s"Involves ${addresses.size} people:")
		addresses.groupBy { _.domain }.foreach { case (domain, addresses) =>
			println(s"\t- $domain: ${
				addresses
					.map { a => a.name.getOrElse { a.address.address.untilFirst("@") } }.mkString(", ")
			}")
		}
		println(s"${thread.messages.size} messages")
		
		// Prints instructions
		if (thread.messages.nonEmpty) {
			println("\nIn order to read the messages in this thread, please use the \"next\" command")
			if (threadQueue.hasNext)
				println("If you want to move to the next thread directly, please use the \"skip\" command")
		}
		else if (threadQueue.nonEmpty)
			println(s"In order to move to the next thread, please use the \"next\" command")
	}
	private def skip() = threadQueue.nextOption() match {
		case Some(thread) => open(thread)
		case None => println("No more threads have been queued")
	}
	
	private def printMessage(message: DetailedMessage) = {
		println()
		println(message)
		
		if (messagesQueue.hasNext) {
			println("\nIn order to print the next message, please use the \"next\" command")
			if (threadQueue.hasNext)
				println(s"If you want to skip directly to the next thread, use the \"skip\" command")
		}
		else if (threadQueue.hasNext)
			println("\nThis was the last message in this thread.\nPlease use the \"next\" command to move to the next thread")
		else
			println("\nThis is the last message in this thread.")
	}
	
	
	// APP CODE   ---------------------------
	
	// A database connection is required during app use
	cPool { implicit c =>
		val searchCommand = Command("find",
			help = "Finds specific message threads based on address and word appearance")(
			ArgumentSchema("people", "p",
				help = "Addresses or names that must be involved in the message threads.\nMay be specified as a json array, or separated by \";\". Partial values are allowed"),
			ArgumentSchema("words", "w",
				help = "Words that must appear on the message threads. Partial values are allowed.\nMay be specified as a json array or separated by \";\""),
			ArgumentSchema("other", "o", help =
				"Other words that may appear within the messages. Used in prioritization. Same rules as with 'words'")
		) { args =>
			val addresses = args("people").getVector.flatMap { _.getString.split(';') }.map { _.trim }.filter { _.nonEmpty }
			val words = Pair(args("words"), args("other"))
				.map { _.getVector.flatMap { _.getString.split(';') }.map { _.trim }.filter { _.nonEmpty } }
			
			if (addresses.isEmpty && words.isEmpty)
				println("Please specify 'people' and/or 'words'")
			else {
				println("\nFinding message threads that: ")
				NotEmpty(addresses).foreach { a =>
					if (a hasSize 1)
						println(s"\t- Involve ${a.head}")
					else
						println(s"\t- Involve at least one person from: ${a.mkString(", ")}")
				}
				NotEmpty(words.first).foreach { w =>
					if (w hasSize 1)
						println(s"\t- Contain word \"${w.head}\"")
					else
						println(s"\t- Contain any of: ${w.map { _.quoted }.mkString(", ")}")
				}
				NotEmpty(words.second).foreach { w =>
					println(s"Prioritizes messages including: ${w.map { _.quoted }.mkString(", ")}")
				}
				
				threadQueue = FindMessages(addresses.toSet, words.first.toSet, words.second.toSet)
				threadQueue.nextOption() match {
					case Some(thread) => open(thread)
					case None => println("\nNo results were found")
				}
			}
		}
		val nextCommand = Command("next", "n", help = "Moves to the next message or the next thread")(
			ArgumentSchema("target",
				help = "\"message\" to move to the next message. \"thread\" to move to the next thread. By default moves to the next message if available, otherwise the next thread.")
		) { args =>
			args("target").getString.headOption match {
				case Some(target) =>
					target.toLower match {
						case 't' => skip()
						case 'm' =>
							messagesQueue.nextOption() match {
								case Some(message) => printMessage(message)
								case None =>
									println("No more messages have been queued")
									if (threadQueue.hasNext)
										println("If you want to open the next thread instead, please use \"next thread\" or \"next\"")
							}
						case _ =>
							println(s"${args("target").getString} is not recognized as a valid target.\nPlease use either \"thread\", \"message\" or leave this value empty.")
					}
				case None =>
					messagesQueue.nextOption() match {
						case Some(message) => printMessage(message)
						case None =>
							threadQueue.nextOption() match {
								case Some(thread) => open(thread)
								case None => println("No queued messages or threads remain")
							}
					}
			}
		}
		val skipCommand = Command.withoutArguments("skip", help = "Moves to the next thread") { skip() }
		
		val commands = Vector(searchCommand, nextCommand, skipCommand)
		println("Welcome to Emissary message search app")
		println("Start with the \"find\" command.")
		println("Use the \"help\" command to get more information about available commands.")
		println("Use the \"exit\" command to close this console.")
		Console.static(commands, "\nNext command:", "exit").run()
	}
	
	println("\nBye!")
}
