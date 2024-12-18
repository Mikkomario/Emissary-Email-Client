package vf.emissary.model.partial.messaging

import utopia.flow.collection.immutable.Pair
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.{InstantType, StringType}
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now
import utopia.flow.util.StringExtensions._
import vf.emissary.model.factory.messaging.AddressFactory

import java.time.Instant

object AddressData extends FromModelFactoryWithSchema[AddressData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Pair(
		PropertyDeclaration("address", StringType),
		PropertyDeclaration("created", InstantType, isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		AddressData(valid("address").getString, valid("created").getInstant)
}

/**
  * Represents an address that represents person or another entity that reads or writes messages.
  * @param address A string representation of this address
  * @param created Time when this address was added to the database
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class AddressData(address: String, created: Instant = Now) 
	extends AddressFactory[AddressData] with ModelConvertible
{
	// COMPUTED	--------------------
	
	/**
	  * The domain part of this address.
	  * E.g. "gmail.com"
	  */
	def domain = address.afterFirst("@")
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Pair("address" -> address, "created" -> created))
	
	override def toString = address
	
	override def withAddress(address: String) = copy(address = address)
	override def withCreated(created: Instant) = copy(created = created)
}

