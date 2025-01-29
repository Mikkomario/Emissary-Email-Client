package vf.emissary.controller.app.command

import utopia.courier.model.Authentication
import utopia.courier.model.read.{ImapReadSettings, ReadSettings}
import utopia.flow.collection.immutable.{Pair, Single}
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.time.Now
import utopia.flow.time.TimeExtensions._
import utopia.flow.util.EitherExtensions._
import utopia.flow.util.StringExtensions._
import utopia.flow.util.console.ConsoleExtensions._
import utopia.flow.util.console.{ArgumentSchema, Command}
import utopia.flow.view.immutable.eventful.AlwaysFalse
import utopia.flow.view.mutable.Pointer
import utopia.flow.view.mutable.async.Volatile
import utopia.flow.view.mutable.eventful.SettableFlag
import vf.emissary.controller.archive.ArchiveEmails
import vf.emissary.database.access.many.messaging.service.user.DbDetailedEmailServiceUsers
import vf.emissary.database.access.single.messaging.address.DbAddress
import vf.emissary.database.access.single.messaging.service.DbEmailService
import vf.emissary.database.access.single.messaging.service.user.DbEmailServiceUser
import vf.emissary.database.storable.messaging.EmailServiceDbModel
import vf.emissary.model.partial.messaging.EmailServiceData
import vf.emissary.database.EmissaryContext._

import java.time.Instant
import scala.concurrent.Future
import scala.io.StdIn

/**
 * Commands for reading and archiving email data from the server
 * @author Mikko Hilpinen
 * @since 22.12.2024, v1.1
 */
object ArchiveCommands
{
	// ATTRIBUTES   -------------------------
	
	private val readSettingsPointer = Pointer.eventful.empty[ReadSettings]
	private val stopArchivingFlagPointer = Volatile.eventful.empty[SettableFlag]
	
	private val archivingFlag = stopArchivingFlagPointer.flatMap {
		case Some(stopFlag) => !stopFlag
		case None => AlwaysFalse
	}
	
	private val loginCommand = Command("login", help = "Starts to operate from the perspective of a specific user")(
		ArgumentSchema("user", "as", help = "Name or email address of the user to log in with")) {
		args =>
			connectionPool.logging { implicit c =>
				// Looks up the existing users
				val users = DbDetailedEmailServiceUsers.pull
				val nameInput = args("user").getString
				val filteredUsers = {
					if (nameInput.isEmpty)
						users
					else
						users.filter { u =>
							u.emailAddress.containsIgnoreCase(nameInput) ||
								u.address.names.exists { _.name.containsIgnoreCase(nameInput) }
						}
				}
				
				// Proposes selection from existing users
				val selected = StdIn.selectFromOrAdd(
					filteredUsers.map { u => (u.service.address, u.emailAddress) -> u.emailAddress }, "users") {
					// Case: User wants to register a new user account => Requests and stores the necessary information
					StdIn.readNonEmptyLine("Please specify the address of the (IMAP) host server").flatMap { host =>
						val serviceNameOrId = DbEmailService(host).id.toRight {
							println("What name do you want to assign to this email service? (optional)")
							StdIn.readLine()
						}
						nameInput.ifNotEmpty
							.filter { a => a.contains('@') && StdIn.ask(s"Should we proceed with email address: $a?") }
							.orElse { StdIn.readNonEmptyLine("Please specify the email address you're using") }
							.map { emailAddress =>
								val addressId = DbAddress(emailAddress).pullOrInsertId().either
								val serviceId = serviceNameOrId.rightOrMap { serviceName =>
									EmailServiceDbModel.insert(EmailServiceData(host, name = serviceName)).id
								}
								DbEmailServiceUser.ofServiceWithAddress(serviceId, addressId).insertIfMissing()
								
								host -> emailAddress
							}
					}
				}
				
				// Requests the password
				selected.foreach { case (host, user) =>
					StdIn.readNonEmptyLine(
						"Please provide the (3rd party application) password for accessing the email server")
						.foreach { password =>
							// Updates the logged in user info
							readSettingsPointer.value = Some(ImapReadSettings(host, Authentication(user, password)))
						}
				}
			}
	}
	
	// NB: Assumes that email settings have been initialized
	private val archiveCommand = Command("archive",
		help = "Downloads, archives and possibly deletes messages from the email server")(
		ArgumentSchema("remove-until", "until", help = "Messages older than this timestamp will be deleted"),
		ArgumentSchema("limit", "max", help = "Maximum number of emails to process. Default = unlimited."),
		ArgumentSchema.flag("delete", "rm", help = "Whether archived messages should be deleted")) {
		args =>
			readSettingsPointer.value.foreach { implicit settings =>
				val removeUntil = args("remove-until").instant.orElse {
					if (args("delete").getBoolean) {
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
				println("Starting the archiving process in the background...")
				println("You can use the stop command (s) to terminate this process")
				// Allows manual stop from the console
				val stopFlag = SettableFlag()
				stopArchivingFlagPointer.setOne(stopFlag)
				Future {
					connectionPool.logging { implicit c =>
						ArchiveEmails(
							readLimit = args("limit").intOr(-1),
							deleteNotAllowedAfter = removeUntil.getOrElse(Instant.EPOCH),
							continueCondition = !stopFlag.value,
							allowMessageDeletion = removeUntil.isDefined)
					}
					stopArchivingFlagPointer.clear()
					println("Email processing completed")
				}
			}
	}
	private val stopArchivingCommand = Command.withoutArguments("stop", "s") {
		stopArchivingFlagPointer.value match {
			case Some(stopFlag) =>
				if (stopFlag.set())
					println("Stopping...")
				else
					println("Already stopping")
				
			case None => println("Already stopped")
		}
	}
	
	/**
	 * A pointer that contains the currently available archiving commands
	 */
	val pointer = readSettingsPointer.mergeWith(archivingFlag) { (settings, archiving) =>
		if (archiving)
			Single(stopArchivingCommand)
		else if (settings.isDefined)
			Pair(archiveCommand, loginCommand)
		else
			Single(loginCommand)
	}
}
