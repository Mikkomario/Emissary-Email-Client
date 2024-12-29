package vf.emissary.controller.archive

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.Empty
import utopia.flow.parse.file.FileExtensions._
import utopia.flow.parse.string.Regex
import utopia.flow.util.EitherExtensions._
import utopia.flow.util.TryExtensions._
import utopia.flow.util.logging.Logger
import utopia.vault.database.Connection
import vf.emissary.database.access.many.messaging.attachment.DbAttachments
import vf.emissary.database.access.many.messaging.message.link.statement.DbMessageStatementLinks

import java.nio.file.Path

/**
 * An interface for cleaning archived messages
 * @author Mikko Hilpinen
 * @since 20.10.2023, v0.1
 */
object CleanArchives
{
	// ATTRIBUTES   ------------------------
	
	private val fileSeparator = Regex.escape('/') || Regex.backslash
	
	
	// OTHER    ----------------------------
	
	/**
	 * Removes message text portions which contain other messages from the same thread (e.g. reply parts)
	 * @param threadId Id of the targeted message thread
	 * @param connection Implicit DB connection
	 * @return Number of statements that were removed from the targeted thread
	 */
	def removeDuplicateTextWithinThread(threadId: Int)(implicit connection: Connection) = {
		// Loads the statement links associated with this thread
		val statementIds = DbMessageStatementLinks.findInThread(threadId).groupBy { _.messageId }
			.view.mapValues { _.sortBy { _.orderIndex } }.toMap
			.withDefaultValue(Empty)
		
		// Checks for duplicate sequences
		val duplicateStatementIds = statementIds.flatMap { case (messageId, statementLinks) =>
			lazy val testedStatementLinks = statementLinks.take(5)
			statementIds.view.filterNot { _._1 == messageId }.flatMap { case (_, otherStatementLinks) =>
				// Case: This message is longer than the one compared => The other can't possibly contain this message
				if (statementLinks.hasSize >= otherStatementLinks)
					Empty
				else {
					// Checks whether the first 5 statements of this message appear somewhere within the other message
					val (placements, _) = testedStatementLinks.foldLeft(otherStatementLinks.indices.toVector -> 0) {
						case ((possiblePlacements, advance), nextLink) =>
							val remainsPossible = possiblePlacements.filter { i =>
								otherStatementLinks.lift(i + advance).exists { _.statementId == nextLink.statementId }
							}
							remainsPossible -> (advance + 1)
					}
					// Will not target duplicate messages
					placements.minOption.filter { _ > 0 } match {
						// Case: This message appeared within the other message => Deletes it from the other message
						case Some(duplicateStartIndex) => otherStatementLinks.drop(duplicateStartIndex).map { _.id }
						// Case: This message didn't appear within the other message
						case None => Empty
					}
				}
			}
		}
		
		// Deletes the duplicates
		if (duplicateStatementIds.nonEmpty)
			DbMessageStatementLinks(duplicateStatementIds.toIntSet).delete()
			
		duplicateStatementIds.toSet.size
	}
	
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
		val recordedAttachments = DbAttachments.relativePaths.view.map { p => fileSeparator.split(p.toJson) }.toSet
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
				if (path.isDirectory) {
					if (path.iterateChildren { _.isEmpty }.getOrElse(false))
						Some(path.delete().map { _ => trashDirectory/relative })
					else
						None
				}
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
					.find { _.hasSameContentAs(targetFile).log.getOrElse(false) }
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
