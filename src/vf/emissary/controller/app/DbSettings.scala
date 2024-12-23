package vf.emissary.controller.app

import utopia.bunnymunch.jawn.JsonBunny
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Model
import utopia.flow.parse.file.FileExtensions._
import utopia.flow.util.TryExtensions._
import utopia.flow.util.console.ConsoleExtensions._
import utopia.vault.database.Connection
import vf.emissary.database.access.many.messaging.message.DbMessages
import vf.emissary.util.Common._

import java.nio.file.Path
import scala.io.StdIn

/**
 * An interface for managing and storing database-accessing settings
 * @author Mikko Hilpinen
 * @since 22.12.2024, v1.1
 */
object DbSettings
{
	// ATTRIBUTES   ------------------
	
	private val settingsPath: Path = "data/settings/db-settings.json"
	
	
	// OTHER    ----------------------
	
	/**
	 * Sets up the database access
	 * @return Whether the database is accessible
	 */
	def setup() = {
		// Looks for previously cached data
		val settings = JsonBunny.munchPath(settingsPath).toOption.flatMap { _.model }.getOrElse(Model.empty)
		
		// Requests the missing data
		val user = settings("user").string
			.orElse { StdIn.readNonEmptyLine("Specify the user to access the local database with (default = root)") }
			.getOrElse("root")
		val password = settings("password").string
			.orElse { StdIn.readNonEmptyLine(
				"Please specify the password to access the database with (empty = no password)") }
			.getOrElse("")
		
		// Attempts to connect to the database
		Connection.modifySettings { _.copy(user = user, password = password, defaultDBName = Some(databaseName)) }
		val accessAttemptResult = cPool.tryWith { implicit c => DbMessages.nonEmpty }
		accessAttemptResult.log
		
		if (accessAttemptResult.isFailure) {
			if (settingsPath.exists)
				settingsPath.delete()
			println(s"Couldn't access the database as $user ${
				if (password.isEmpty) "without a password" else "with the specified password" }")
			false
		}
		else {
			if (!settings.contains("user")) {
				val saveModel = {
					if (password.nonEmpty && StdIn.ask("Do you want to save this password?"))
						Model.from("user" -> user, "password" -> password)
					else
						Model.from("user" -> user)
				}
				settingsPath.createParentDirectories().flatMap { _.writeJson(saveModel) }
					.logWithMessage("Failed to save database access settings")
			}
			true
		}
	}
}
