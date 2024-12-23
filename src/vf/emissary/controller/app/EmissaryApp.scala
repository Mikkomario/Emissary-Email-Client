package vf.emissary.controller.app

import utopia.flow.util.console.Console
import vf.emissary.controller.app.command.ArchiveCommands
import vf.emissary.util.Common._

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
		val commandsPointer = ArchiveCommands.pointer
		val commandNamesPointer = commandsPointer.map { _.map { _.name }.sorted.mkString(", ") }
		
		println("Welcome to Emissary email-archive application!")
		
		Console(commandsPointer,
			prompt = s"\nPlease specify the next command (${ commandNamesPointer.value }, exit)",
			closeCommandName = "exit")
			.run()
		
		println("\nSee you next time!")
	}
	else
		println("Closing. Please make sure the local database is accessible and properly set up.")
}
