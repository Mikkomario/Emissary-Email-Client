package vf.emissary.model.partial.text

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.StringType
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now

import java.time.Instant

object DelimiterData extends FromModelFactoryWithSchema[DelimiterData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("text", StringType), PropertyDeclaration("created", 
			InstantType, isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		DelimiterData(valid("text").getString, valid("created").getInstant)
}

/**
  * Represents a character sequence used to separate two statements or parts of a statement
  * @param text The characters that form this delimiter
  * @param created Time when this delimiter was added to the database
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class DelimiterData(text: String, created: Instant = Now) extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("text" -> text, "created" -> created))
}

