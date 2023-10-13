package vf.emissary.database.model.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.emissary.database.factory.messaging.AttachmentFactory
import vf.emissary.model.partial.messaging.AttachmentData
import vf.emissary.model.stored.messaging.Attachment

/**
  * Used for constructing AttachmentModel instances and for inserting attachments to the database
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
object AttachmentModel extends DataInserter[AttachmentModel, Attachment, AttachmentData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains attachment message id
	  */
	val messageIdAttName = "messageId"
	
	/**
	  * Name of the property that contains attachment original file name
	  */
	val originalFileNameAttName = "originalFileName"
	
	/**
	  * Name of the property that contains attachment stored file name
	  */
	val storedFileNameAttName = "storedFileName"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains attachment message id
	  */
	def messageIdColumn = table(messageIdAttName)
	
	/**
	  * Column that contains attachment original file name
	  */
	def originalFileNameColumn = table(originalFileNameAttName)
	
	/**
	  * Column that contains attachment stored file name
	  */
	def storedFileNameColumn = table(storedFileNameAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = AttachmentFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: AttachmentData) = 
		apply(None, Some(data.messageId), data.originalFileName, data.storedFileName)
	
	override protected def complete(id: Value, data: AttachmentData) = Attachment(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param id A attachment id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param messageId Id of the message to which this file is attached
	  * @return A model containing only the specified message id
	  */
	def withMessageId(messageId: Int) = apply(messageId = Some(messageId))
	
	/**
	  * @param originalFileName Name of the attached file, as it was originally sent
	  * @return A model containing only the specified original file name
	  */
	def withOriginalFileName(originalFileName: String) = apply(originalFileName = originalFileName)
	
	/**
	  * @param storedFileName Name of the attached file, 
	  * as it appears on the local file system. Empty if identical to the original file name.
	  * @return A model containing only the specified stored file name
	  */
	def withStoredFileName(storedFileName: String) = apply(storedFileName = storedFileName)
}

/**
  * Used for interacting with Attachments in the database
  * @param id attachment database id
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
case class AttachmentModel(id: Option[Int] = None, messageId: Option[Int] = None, 
	originalFileName: String = "", storedFileName: String = "") 
	extends StorableWithFactory[Attachment]
{
	// IMPLEMENTED	--------------------
	
	override def factory = AttachmentModel.factory
	
	override def valueProperties = {
		import AttachmentModel._
		Vector("id" -> id, messageIdAttName -> messageId, originalFileNameAttName -> originalFileName, 
			storedFileNameAttName -> storedFileName)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param messageId Id of the message to which this file is attached
	  * @return A new copy of this model with the specified message id
	  */
	def withMessageId(messageId: Int) = copy(messageId = Some(messageId))
	
	/**
	  * @param originalFileName Name of the attached file, as it was originally sent
	  * @return A new copy of this model with the specified original file name
	  */
	def withOriginalFileName(originalFileName: String) = copy(originalFileName = originalFileName)
	
	/**
	  * @param storedFileName Name of the attached file, 
	  * as it appears on the local file system. Empty if identical to the original file name.
	  * @return A new copy of this model with the specified stored file name
	  */
	def withStoredFileName(storedFileName: String) = copy(storedFileName = storedFileName)
}

