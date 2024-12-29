package vf.emissary.database.storable.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.flow.parse.file.FileExtensions._
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{FromIdFactory, HasId, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.factory.messaging.AttachmentFactory
import vf.emissary.model.partial.messaging.AttachmentData
import vf.emissary.model.stored.messaging.Attachment

import java.nio.file.Path

/**
  * Used for constructing AttachmentDbModel instances and for inserting attachments to the 
  * database
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object AttachmentDbModel 
	extends StorableFactory[AttachmentDbModel, Attachment, AttachmentData] 
		with FromIdFactory[Int, AttachmentDbModel] with HasIdProperty 
		with AttachmentFactory[AttachmentDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with relative paths
	  */
	lazy val relativePath = property("relativePath")
	
	/**
	  * Database property used for interacting with sizes
	  */
	lazy val size = property("size")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = EmissaryTables.attachment
	
	override def apply(data: AttachmentData): AttachmentDbModel = 
		apply(None, data.relativePath.toJson, Some(data.size))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	/**
	  * @param relativePath Name of the attached file, as appears on the file system
	  * @return A model containing only the specified relative path
	  */
	override def withRelativePath(relativePath: Path) = apply(relativePath = relativePath.toJson)
	
	/**
	  * @param size Size of this attachment in bytes
	  * @return A model containing only the specified size
	  */
	override def withSize(size: Long) = apply(size = Some(size))
	
	override protected def complete(id: Value, data: AttachmentData) = Attachment(id.getInt, data)
}

/**
  * Used for interacting with Attachments in the database
  * @param id attachment database id
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
case class AttachmentDbModel(id: Option[Int] = None, relativePath: String = "", size: Option[Long] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, AttachmentDbModel] 
		with AttachmentFactory[AttachmentDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties = 
		Vector(AttachmentDbModel.id.name -> id, AttachmentDbModel.relativePath.name -> relativePath, 
			AttachmentDbModel.size.name -> size)
	
	
	// IMPLEMENTED	--------------------
	
	override def table = AttachmentDbModel.table
	
	override def withId(id: Int) = copy(id = Some(id))
	
	/**
	  * @param relativePath Name of the attached file, as appears on the file system
	  * @return A new copy of this model with the specified relative path
	  */
	override def withRelativePath(relativePath: Path) = copy(relativePath = relativePath.toJson)
	
	/**
	  * @param size Size of this attachment in bytes
	  * @return A new copy of this model with the specified size
	  */
	override def withSize(size: Long) = copy(size = Some(size))
}

