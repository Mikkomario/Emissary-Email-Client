package vf.emissary.model.partial.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.{InstantType, StringType}
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now

import java.time.Instant

object AddressData extends FromModelFactoryWithSchema[AddressData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(
			PropertyDeclaration("address", StringType),
			PropertyDeclaration("created", InstantType, isOptional = true)
		))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		AddressData(valid("address").getString, valid("created").getInstant)
}

/**
  * Represents an address that represents person or another entity that reads or writes messages.
  * @param created Time when this address was added to the database
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class AddressData(address: String, created: Instant = Now) extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("address" -> address, "created" -> created))
	
	override def toString = address
}

