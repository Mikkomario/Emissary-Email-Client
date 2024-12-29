package vf.emissary.controller.app

import utopia.flow.parse.file.FileExtensions._
import utopia.flow.util.console.Console
import utopia.flow.util.TryExtensions._
import utopia.vault.database.columnlength.ColumnLengthRules
import vf.emissary.controller.app.command.{ArchiveCommands, CleanCommands, PeopleCommands, SearchCommands}
import vf.emissary.util.Common._

import java.nio.file.Paths

/**
 * The main command line application for this project
 *
 * @author Mikko Hilpinen
 * @since 22.12.2024, v1.1
 */
object EmissaryApp extends App
{
	// APP CODE -------------------------
	
	// Sets up DB connection settings
	if (DbSettings.setup()) {
		// Sets up the length rules
		Paths.get("data/length-rules")
			.iterateChildren { _.map { ColumnLengthRules.loadFrom(_, databaseName) }.toTryCatch }.flattenCatching
			.logWithMessage("Failed to apply some or all of the length rules")
		
		val commandsPointer = SearchCommands.pointer
			.mergeWith(ArchiveCommands.pointer) { _ ++ _ ++ PeopleCommands ++ CleanCommands }
		val commandNamesPointer = commandsPointer.map { _.map { _.name }.sorted.mkString(", ") }
		
		println("Welcome to Emissary email-archive application!")
		
		Console(commandsPointer,
			prompt = s"\nPlease specify the next command (${ commandNamesPointer.value }, exit)",
			closeCommandName = "exit")
			.run()
		
		println("\nSee you next time!")
		SearchCommands.closeOpenSearch()
	}
	else
		println("Closing. Please make sure the local database is accessible and properly set up.")
}
