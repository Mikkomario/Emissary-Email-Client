package vf.emissary.database.access.many.messaging.attachment

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.parse.file.FileExtensions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import vf.emissary.database.storable.messaging.AttachmentDbModel

import java.nio.file.Path

/**
  * A common trait for access points which target multiple attachments or similar instances at a 
  * time
  * @tparam A Type of read (attachments -like) instances
  * @tparam Repr Type of this access point
  * @author Mikko Hilpinen
  * @since 27.12.2024, v1.1
  */
trait ManyAttachmentsAccessLike[+A, +Repr] extends ManyModelAccess[A] with Indexed with FilterableView[Repr]
{
	// COMPUTED	--------------------
	
	/**
	 * Access to attachments that are not stored in any subdirectory
	 */
	def inAttachmentsRootDirectory = filter(!model.relativePath.like("%/%"))
	
	/**
	  * relative paths of the accessible attachments
	  */
	def relativePaths(implicit connection: Connection) = 
		pullColumn(model.relativePath.column).flatMap { _.string }.map { v => v: Path }
	/**
	  * sizes of the accessible attachments
	  */
	def sizes(implicit connection: Connection) = pullColumn(model.size.column).map { v => v.getLong }
	/**
	  * Unique ids of the accessible attachments
	  */
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = AttachmentDbModel
	
	
	// OTHER	--------------------
	
	/**
	  * @param relativePath relative path to target
	  * @return Copy of this access point that only includes attachments with the specified relative path
	  */
	def withRelativePath(relativePath: Path) = filter(model.relativePath.column <=> relativePath.toJson)
	/**
	  * @param relativePaths Targeted relative paths
	  * @return Copy of this access point that only includes attachments where relative path is within the 
	  * specified value set
	  */
	def withRelativePaths(relativePaths: Iterable[Path]) = 
		filter(model.relativePath.column.in(relativePaths.map { relativePath => relativePath.toJson }))
	
	/**
	 * @param relativeDir A directory path relative to the attachments root directory
	 * @return Access to attachments stored within the specified directory
	 */
	def inRelativeDirectory(relativeDir: Path) = filter(model.relativePath.like(s"${ relativeDir.toJson }/%"))
	
	/**
	  * @param size size to target
	  * @return Copy of this access point that only includes attachments with the specified size
	  */
	def withSize(size: Long) = filter(model.size.column <=> size)
	/**
	  * @param sizes Targeted sizes
	  * @return Copy of this access point that only includes attachments where size is within the specified 
	  * value set
	  */
	def withSizes(sizes: Iterable[Long]) = filter(model.size.column.in(sizes))
}

