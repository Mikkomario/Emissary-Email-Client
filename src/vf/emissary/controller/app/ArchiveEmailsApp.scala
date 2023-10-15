package vf.emissary.controller.app

import utopia.courier.model.Authentication
import utopia.courier.model.read.{ImapReadSettings, ReadSettings}
import utopia.flow.async.AsyncExtensions._
import utopia.flow.collection.CollectionExtensions._
import utopia.flow.parse.file.FileExtensions._
import utopia.flow.parse.json.{JsonParser, JsonReader}
import utopia.flow.time.Now
import utopia.flow.time.TimeExtensions._
import utopia.flow.util.console.ConsoleExtensions._
import utopia.vault.database.columnlength.ColumnLengthRules
import vf.emissary.controller.archive.ArchiveEmails

import java.nio.file.Paths
import scala.io.StdIn

/**
 * An application used for archiving email data
 * @author Mikko Hilpinen
 * @since 15.10.2023, v0.1
 */
object ArchiveEmailsApp extends App
{
	import vf.emissary.util.Common._
	implicit val jsonParser: JsonParser = JsonReader
	
	// Sets up length rules
	Paths.get("data/length-rules").tryIterateChildren { _.tryForeach(ColumnLengthRules.loadFrom) }.get
	
	StdIn.readNonEmptyLine("Please specify SMTP host address").foreach { host =>
		StdIn.readNonEmptyLine("Please specify your email address").foreach { emailAddress =>
			StdIn.readNonEmptyLine("Please specify your (3rd party app) email password").foreach { pw =>
				implicit val readSettings: ReadSettings = ImapReadSettings(host, Authentication(emailAddress, pw))
				println("Starting the archiving process...")
				ArchiveEmails("data/test-data/attachments", Some(Now - 2.weeks)).waitForResult().get
				println("Process completed")
			}
		}
	}
	
	println("Bye!")
}
