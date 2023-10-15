package vf.emissary.model.partial.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.mutable.DataType.StringType
import utopia.flow.generic.model.template.ModelConvertible

object AttachmentData extends FromModelFactoryWithSchema[AttachmentData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("messageId", IntType, Vector("message_id")), 
			PropertyDeclaration("fileName", StringType, Vector("file_name"))))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		AttachmentData(valid("messageId").getInt, valid("fileName").getString)
}

/**
  * Represents an attached file within a message
  * @param messageId Id of the message to which this file is attached
  * @param fileName Name of the attached file
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
case class AttachmentData(messageId: Int, fileName: String)
	extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("messageId" -> messageId, "fileName" -> fileName))
}

