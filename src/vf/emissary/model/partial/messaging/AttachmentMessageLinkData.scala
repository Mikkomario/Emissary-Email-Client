package vf.emissary.model.partial.messaging

import utopia.flow.collection.immutable.{Pair, Single}
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.template.ModelConvertible
import vf.emissary.model.factory.messaging.AttachmentMessageLinkFactory

object AttachmentMessageLinkData extends FromModelFactoryWithSchema[AttachmentMessageLinkData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Pair(PropertyDeclaration("attachmentId", IntType, Single("attachment_id")), 
			PropertyDeclaration("messageId", IntType, Single("message_id"))))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		AttachmentMessageLinkData(valid("attachmentId").getInt, valid("messageId").getInt)
}

/**
  * Links an attachment to the messages in which it appears
  * @param attachmentId Id of the linked attachment
  * @param messageId    Id of the message in which the attachment appears
  * @author Mikko Hilpinen
  * @since 27.12.2024, v1.1
  */
case class AttachmentMessageLinkData(attachmentId: Int, messageId: Int) 
	extends AttachmentMessageLinkFactory[AttachmentMessageLinkData] with ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Pair("attachmentId" -> attachmentId, "messageId" -> messageId))
	
	override def withAttachmentId(attachmentId: Int) = copy(attachmentId = attachmentId)
	
	override def withMessageId(messageId: Int) = copy(messageId = messageId)
}

