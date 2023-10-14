package vf.emissary.model.partial.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now

import java.time.Instant

object MessageThreadData extends FromModelFactoryWithSchema[MessageThreadData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(PropertyDeclaration("created", InstantType, isOptional = true))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = MessageThreadData(valid("created").getInstant)
}

/**
  * Represents a subject or a header given to a sequence of messages
  * @param created Time when this thread was opened
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class MessageThreadData(created: Instant = Now) extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("created" -> created))
}

