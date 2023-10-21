package vf.emissary.controller.app

import utopia.courier.model.Authentication
import utopia.courier.model.read.{ImapReadSettings, ReadSettings}
import utopia.flow.collection.CollectionExtensions._
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.parse.file.FileExtensions._
import utopia.flow.time.Now
import utopia.flow.util.console.ConsoleExtensions._
import utopia.flow.util.StringExtensions._
import utopia.flow.time.TimeExtensions._
import utopia.flow.view.mutable.async.VolatileFlag
import utopia.vault.database.columnlength.ColumnLengthRules
import vf.emissary.controller.archive.ArchiveEmails

import java.nio.file.Paths
import java.time.Instant
import scala.concurrent.Future
import scala.io.StdIn

/**
 * An application used for archiving email data
 * @author Mikko Hilpinen
 * @since 15.10.2023, v0.1
 */
object ArchiveEmailsApp extends App
{
	import vf.emissary.util.Common._
	
	// TODO: Set up database settings
	
	// Sets up length rules
	Paths.get("data/length-rules").tryIterateChildren { _.tryForeach(ColumnLengthRules.loadFrom) }.get
	
	StdIn.readNonEmptyLine("Please specify SMTP host address").foreach { host =>
		StdIn.readNonEmptyLine("Please specify your email address").foreach { emailAddress =>
			StdIn.readNonEmptyLine("Please specify your (3rd party app) email password").foreach { pw =>
				implicit val readSettings: ReadSettings = ImapReadSettings(host, Authentication(emailAddress, pw))
				val deletionAllowed = StdIn.ask("Should processed messages be deleted from the server (up to a certain send-time)?")
				val deleteNotAllowedAfter = {
					if (deletionAllowed) {
						println("How old messages should be preserved?")
						println("Options: year | month | week. You can also specify a count, e.g. \"2 years\"")
						val parts = StdIn.readLine().trim.splitAtFirst(" ").map { _.trim }
						val (count, unit) = parts.first.int match {
							case Some(count) => (count, parts.second)
							case None => (1, parts.first)
						}
						val since = unit.toLowerCase.headOption.getOrElse(' ') match {
							case 'y' => Some(Now - count.years.toApproximateDuration)
							case 'm' => Some(Now - count.months.toApproximateDuration)
							case 'w' => Some(Now - count.weeks)
							case _ =>
								println(s"Unrecognized unit '$unit' => Deletion is disabled")
								None
						}
						since.foreach { since => println(s"Messages sent later than $since will be preserved") }
						since
					}
					else
						None
				}
				println("Starting the archiving process...")
				// Allows manual stop from the console
				val stopFlag = VolatileFlag()
				Future {
					while (stopFlag.isNotSet) {
						println("If you want to stop the email processing, please write stop and press enter.")
						if (StdIn.readLine().toLowerCase.trim == "stop") {
							stopFlag.set()
							println("Stopping...")
						}
					}
				}
				cPool { implicit c =>
					ArchiveEmails("data/test-data/attachments",
						deleteNotAllowedAfter = deleteNotAllowedAfter.getOrElse(Instant.EPOCH),
						continueCondition = !stopFlag.value,
						allowMessageDeletion = deletionAllowed && deleteNotAllowedAfter.isDefined)
				}
				println("Email processing completed")
			}
		}
	}
	
	println("Bye!")
}
