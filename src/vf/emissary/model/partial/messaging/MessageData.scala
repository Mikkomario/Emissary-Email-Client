package vf.emissary.model.partial.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.mutable.DataType.StringType
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now

import java.time.Instant

object MessageData extends FromModelFactoryWithSchema[MessageData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("threadId", IntType, Vector("thread_id")), 
			PropertyDeclaration("senderId", IntType, Vector("sender_id")), PropertyDeclaration("messageId", 
			StringType, Vector("message_id"), isOptional = true), PropertyDeclaration("replyToId", IntType, 
			Vector("reply_to_id"), isOptional = true), PropertyDeclaration("created", InstantType, 
			isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		MessageData(valid("threadId").getInt, valid("senderId").getInt, valid("messageId").getString, 
			valid("replyToId").int, valid("created").getInstant)
}

/**
  * Represents a message sent between two or more individuals or entities
  * @param threadId Id of the thread to which this message belongs
  * @param senderId Id of the address from which this message was sent
  * @param messageId (Unique) id given to this message by the sender
  * @param replyToId Id of the message this message replies to, if applicable
  * @param created Time when this message was sent
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class MessageData(threadId: Int, senderId: Int, messageId: String = "", replyToId: Option[Int] = None, 
	created: Instant = Now) 
	extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("threadId" -> threadId, "senderId" -> senderId, "messageId" -> messageId, 
			"replyToId" -> replyToId, "created" -> created))
}

