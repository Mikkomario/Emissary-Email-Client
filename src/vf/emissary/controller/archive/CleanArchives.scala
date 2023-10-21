package vf.emissary.controller.archive

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.parse.file.FileExtensions._
import utopia.flow.parse.string.Regex
import utopia.flow.util.logging.Logger
import utopia.vault.database.Connection
import vf.emissary.database.access.many.messaging.attachment.DbAttachments

import java.nio.file.Path
import scala.collection.mutable

/**
 * An interface for cleaning archived messages
 * @author Mikko Hilpinen
 * @since 20.10.2023, v0.1
 */
object CleanArchives
{
	private val fileSeparator = Regex.escape('/') || Regex.backslash
	
	/**
	 * Deletes (i.e. separates) all saved attachment files that are not referenced in the database
	 * @param attachmentsDirectory Directory where all attachments are stored
	 * @param trashDirectory Directory where non-referenced attachments should be moved
	 * @param connection Implicit DB connection
	 * @param log Implicit logging implementation
	 * @return Failed move attempts + successfully moved files
	 */
	def deleteUnreferencedAttachments(attachmentsDirectory: Path, trashDirectory: Path)
	                                 (implicit connection: Connection, log: Logger) =
	{
		// Finds all attachment file names listed in the database
		val recordedAttachments = DbAttachments.fileNames.toSet.map(fileSeparator.split)
		// Goes through all saved attachment files
		attachmentsDirectory.toTree
			.map { p =>
				val relative = p.relativeTo(attachmentsDirectory).either
				val parts = relative.parts
				(p, relative, parts)
			}
			// Checks whether that file exists in the database
			.bottomToTopNodesIterator.flatMap { node =>
				val (path, relative, parts) = node.nav
				// Case: Directory => Empty directories are deleted
				if (path.isDirectory)
					Some(path.delete().map { _ => trashDirectory/relative })
				// Case: File exists in the database => Leaves it as is
				else if (recordedAttachments.contains(parts))
					None
				// Case: File doesn't exist in the database => Moves it to the trash directory
				else {
					val targetPath = trashDirectory/relative
					Some(targetPath.createDirectories().flatMap { path.moveAs(_) })
				}
			}
			// Separates successes and failures
			.divided
	}
	
	def deleteDuplicateAttachments(attachmentsDirectory: Path)(implicit connection: Connection, log: Logger) = {
		val replacements = attachmentsDirectory.toTree.nodesBelowIterator.filter { _.hasChildren }.flatMap { node =>
			val files = node.children.filter { _.isEmpty }.map { _.nav }.toVector.sortBy { _.fileName }
			files.indices.flatMap { i =>
				val targetFile = files(i)
				files.view.drop(i + 1)
					.find { _.hasSameContentAs(targetFile).getOrElseLog(false) }
					.map { identicalPath => (targetFile, identicalPath) }
			}
		}.toMap
		
	}
	
	private def simplifyReplacements(replacements: Map[Path, Path]) = {
		// TODO: This doesn't work => Implement one that works (probably tree-based?)
		/*
		val lockedPaths = mutable.Set[Path]()
		replacements.map { case (from, to) =>
			if (lockedPaths.contains(from))
				from -> to
			else
				replacements.get(to) match {
					case Some(newTo) =>
						lockedPaths += newTo
						from -> newTo
					case None => from -> to
				}
		}
		 */
	}
}
