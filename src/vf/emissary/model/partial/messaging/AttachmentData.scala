package vf.emissary.model.partial.messaging

import utopia.flow.collection.immutable.{Pair, Single}
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.{IntType, StringType}
import utopia.flow.generic.model.template.ModelConvertible
import vf.emissary.model.factory.messaging.AttachmentFactory

object AttachmentData extends FromModelFactoryWithSchema[AttachmentData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Pair(
		PropertyDeclaration("messageId", IntType, Single("message_id")),
		PropertyDeclaration("fileName", StringType, Single("file_name"))))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		AttachmentData(valid("messageId").getInt, valid("fileName").getString)
}

/**
  * Represents an attached file within a message
  * @param messageId Id of the message to which this file is attached
  * @param fileName Name of the attached file, as appears on the file system
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
case class AttachmentData(messageId: Int, fileName: String) 
	extends AttachmentFactory[AttachmentData] with ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Pair("messageId" -> messageId, "fileName" -> fileName))
	
	override def withFileName(fileName: String) = copy(fileName = fileName)
	override def withMessageId(messageId: Int) = copy(messageId = messageId)
}

