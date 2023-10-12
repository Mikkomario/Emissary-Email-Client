package vf.emissary.model.partial.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now

import java.time.Instant

object MessageThreadData extends FromModelFactoryWithSchema[MessageThreadData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("authorId", IntType, Vector("author_id")), 
			PropertyDeclaration("created", InstantType, isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		MessageThreadData(valid("authorId").getInt, valid("created").getInstant)
}

/**
  * Represents a subject or a header given to a sequence of messages
  * @param authorId Id of the address / entity that originated this thread
  * @param created Time when this thread was opened
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class MessageThreadData(authorId: Int, created: Instant = Now) extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("authorId" -> authorId, "created" -> created))
}

