package vf.emissary.model.factory.messaging

import java.nio.file.Path

/**
  * Common trait for attachment-related factories which allow construction with individual 
  * properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait AttachmentFactory[+A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param relativePath New relative path to assign
	  * @return Copy of this item with the specified relative path
	  */
	def withRelativePath(relativePath: Path): A
	
	/**
	  * @param size New size to assign
	  * @return Copy of this item with the specified size
	  */
	def withSize(size: Long): A
}

