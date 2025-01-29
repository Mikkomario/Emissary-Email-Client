package vf.emissary.controller.app

import utopia.flow.parse.file.FileExtensions._
import vf.emissary.controller.archive.CleanArchives
import vf.emissary.database.EmissaryContext._

import java.nio.file.Path

/**
 * Cleans the unreferenced attachments
 * @author Mikko Hilpinen
 * @since 21.10.2023, v0.1
 */
// TODO: Convert to a command instead
@deprecated("This application shall be replaced with a console command", "v1.1")
object CleanArchivesApp extends App
{
	// NB: Setup is missing
	
	val attachmentsDirectory: Path = "data/test-data/attachments"
	val trashDirectory: Path = "data/test-data/attachments-to-delete"
	
	println("Processing attachments...")
	connectionPool { implicit c => CleanArchives.deleteUnreferencedAttachments(attachmentsDirectory, trashDirectory) }
	
	println("Done!")
	trashDirectory.openInDesktop()
}
