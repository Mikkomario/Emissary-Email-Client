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

object PendingThreadReferenceData extends FromModelFactoryWithSchema[PendingThreadReferenceData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("threadId", IntType, Vector("thread_id")), 
			PropertyDeclaration("referencedMessageId", StringType, Vector("referenced_message_id")), 
			PropertyDeclaration("created", InstantType, isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		PendingThreadReferenceData(valid("threadId").getInt, valid("referencedMessageId").getString, 
			valid("created").getInstant)
}

/**
  * Used for documenting those message ids involved within threads, 
	that have not been linked to any read message
  * @param threadId Id of the message thread with which the referenced message is linked to
  * @param referencedMessageId Message id belonging to some unread message in the linked thread
  * @param created Time when this pending thread reference was added to the database
  * @author Mikko Hilpinen
  * @since 19.10.2023, v0.1
  */
case class PendingThreadReferenceData(threadId: Int, referencedMessageId: String, created: Instant = Now) 
	extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("threadId" -> threadId, "referencedMessageId" -> referencedMessageId, 
			"created" -> created))
}

