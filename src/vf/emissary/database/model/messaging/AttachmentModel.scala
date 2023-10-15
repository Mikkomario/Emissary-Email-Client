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
	  * Name of the property that contains attachment file name
	  */
	val fileNameAttName = "fileName"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains attachment message id
	  */
	def messageIdColumn = table(messageIdAttName)
	
	/**
	  * Column that contains attachment file name
	  */
	def fileNameColumn = table(fileNameAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = AttachmentFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: AttachmentData) = apply(None, Some(data.messageId), data.fileName)
	
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
	  * @param fileName Name of the attached file
	  * @return A model containing only the specified original file name
	  */
	def withOriginalFileName(fileName: String) = apply(fileName = fileName)
}

/**
  * Used for interacting with Attachments in the database
  * @param id attachment database id
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
case class AttachmentModel(id: Option[Int] = None, messageId: Option[Int] = None,
                           fileName: String = "")
	extends StorableWithFactory[Attachment]
{
	// IMPLEMENTED	--------------------
	
	override def factory = AttachmentModel.factory
	
	override def valueProperties = {
		import AttachmentModel._
		Vector("id" -> id, messageIdAttName -> messageId, fileNameAttName -> fileName)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param messageId Id of the message to which this file is attached
	  * @return A new copy of this model with the specified message id
	  */
	def withMessageId(messageId: Int) = copy(messageId = Some(messageId))
	
	/**
	  * @param fileName Name of the attached file, as it was originally sent
	  * @return A new copy of this model with the specified original file name
	  */
	def withFileName(fileName: String) = copy(fileName = fileName)
}

