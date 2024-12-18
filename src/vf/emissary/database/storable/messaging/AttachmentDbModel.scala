package vf.emissary.database.storable.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{FromIdFactory, HasId, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.factory.messaging.AttachmentFactory
import vf.emissary.model.partial.messaging.AttachmentData
import vf.emissary.model.stored.messaging.Attachment

/**
  * Used for constructing AttachmentDbModel instances and for inserting attachments to the database
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
	  * Database property used for interacting with message ids
	  */
	lazy val messageId = property("messageId")
	
	/**
	  * Database property used for interacting with file names
	  */
	lazy val fileName = property("fileName")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = EmissaryTables.attachment
	
	override def apply(data: AttachmentData): AttachmentDbModel = apply(None, Some(data.messageId), 
		data.fileName)
	
	/**
	  * @param fileName Name of the attached file, as appears on the file system
	  * @return A model containing only the specified file name
	  */
	override def withFileName(fileName: String) = apply(fileName = fileName)
	
	override def withId(id: Int) = apply(id = Some(id))
	
	/**
	  * @param messageId Id of the message to which this file is attached
	  * @return A model containing only the specified message id
	  */
	override def withMessageId(messageId: Int) = apply(messageId = Some(messageId))
	
	override protected def complete(id: Value, data: AttachmentData) = Attachment(id.getInt, data)
}

/**
  * Used for interacting with Attachments in the database
  * @param id attachment database id
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
case class AttachmentDbModel(id: Option[Int] = None, messageId: Option[Int] = None, fileName: String = "") 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, AttachmentDbModel] 
		with AttachmentFactory[AttachmentDbModel]
{
	// IMPLEMENTED	--------------------
	
	override def table = AttachmentDbModel.table
	
	override def valueProperties = 
		Vector(AttachmentDbModel.id.name -> id, AttachmentDbModel.messageId.name -> messageId, 
			AttachmentDbModel.fileName.name -> fileName)
	
	/**
	  * @param fileName Name of the attached file, as appears on the file system
	  * @return A new copy of this model with the specified file name
	  */
	override def withFileName(fileName: String) = copy(fileName = fileName)
	
	override def withId(id: Int) = copy(id = Some(id))
	
	/**
	  * @param messageId Id of the message to which this file is attached
	  * @return A new copy of this model with the specified message id
	  */
	override def withMessageId(messageId: Int) = copy(messageId = Some(messageId))
}

