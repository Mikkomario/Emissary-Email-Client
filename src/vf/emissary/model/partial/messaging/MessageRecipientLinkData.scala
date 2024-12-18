package vf.emissary.model.partial.messaging

import utopia.flow.collection.immutable.{Empty, Single}
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.template.ModelConvertible
import vf.emissary.model.enumeration.RecipientType
import vf.emissary.model.enumeration.RecipientType.Primary
import vf.emissary.model.factory.messaging.MessageRecipientLinkFactory

object MessageRecipientLinkData extends FromModelFactoryWithSchema[MessageRecipientLinkData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("messageId", IntType, Single("message_id")),
		PropertyDeclaration("recipientId", IntType, Single("recipient_id")),
		PropertyDeclaration("role", IntType, Empty, Primary.id)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		MessageRecipientLinkData(valid("messageId").getInt, valid("recipientId").getInt, 
			RecipientType.fromValue(valid("role")))
}

/**
  * Links a message to it's assigned recipients
  * @param messageId Id of the sent message
  * @param recipientId Id of the message recipient (address)
  * @param role Role / type of the message recipient
  * @author Mikko Hilpinen
  * @since 15.10.2023, v0.1
  */
case class MessageRecipientLinkData(messageId: Int, recipientId: Int, role: RecipientType = Primary) 
	extends MessageRecipientLinkFactory[MessageRecipientLinkData] with ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("messageId" -> messageId, "recipientId" -> recipientId, "role" -> role.id))
	
	override def withMessageId(messageId: Int) = copy(messageId = messageId)
	override def withRecipientId(recipientId: Int) = copy(recipientId = recipientId)
	override def withRole(role: RecipientType) = copy(role = role)
}

