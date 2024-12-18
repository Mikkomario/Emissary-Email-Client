package vf.emissary.model.partial.messaging

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.{InstantType, IntType, StringType}
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now
import vf.emissary.model.factory.messaging.PendingReplyReferenceFactory

import java.time.Instant

object PendingReplyReferenceData extends FromModelFactoryWithSchema[PendingReplyReferenceData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("messageId", IntType, Single("message_id")),
		PropertyDeclaration("referencedMessageId", StringType, Single("referenced_message_id")),
		PropertyDeclaration("created", InstantType, isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		PendingReplyReferenceData(valid("messageId").getInt, valid("referencedMessageId").getString, 
			valid("created").getInstant)
}

/**
  * Documents an unresolved reference made from a reply message
  * @param messageId Id of the message from which this reference is made from
  * @param referencedMessageId Message id of the referenced message
  * @param created Time when this pending reply reference was added to the database
  * @author Mikko Hilpinen
  * @since 19.10.2023, v0.1
  */
case class PendingReplyReferenceData(messageId: Int, referencedMessageId: String, created: Instant = Now) 
	extends PendingReplyReferenceFactory[PendingReplyReferenceData] with ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("messageId" -> messageId, "referencedMessageId" -> referencedMessageId, 
			"created" -> created))
	
	override def withCreated(created: Instant) = copy(created = created)
	
	override def withMessageId(messageId: Int) = copy(messageId = messageId)
	
	override def withReferencedMessageId(referencedMessageId: String) = 
		copy(referencedMessageId = referencedMessageId)
}

