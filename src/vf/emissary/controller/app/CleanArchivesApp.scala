package vf.emissary.controller.app

import utopia.flow.parse.file.FileExtensions._
import vf.emissary.controller.archive.CleanArchives
import vf.emissary.util.Common._

import java.nio.file.Path

/**
 * Cleans the unreferenced attachments
 * @author Mikko Hilpinen
 * @since 21.10.2023, v0.1
 */
object CleanArchivesApp extends App
{
	val attachmentsDirectory: Path = "data/test-data/attachments"
	val trashDirectory: Path = "data/test-data/attachments-to-delete"
	
	println("Processing attachments...")
	cPool { implicit c => CleanArchives.deleteUnreferencedAttachments(attachmentsDirectory, trashDirectory) }
	
	println("Done!")
	trashDirectory.openInDesktop()
}
