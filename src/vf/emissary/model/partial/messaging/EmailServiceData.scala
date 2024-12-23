package vf.emissary.model.partial.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.StringType
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now
import vf.emissary.model.factory.messaging.EmailServiceFactory

import java.time.Instant

object EmailServiceData extends FromModelFactoryWithSchema[EmailServiceData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("address", StringType), PropertyDeclaration("created", 
			InstantType, isOptional = true), PropertyDeclaration("name", StringType, isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		EmailServiceData(valid("address").getString, valid("created").getInstant, valid("name").getString)
}

/**
  * Represents a server / service which manages emails
  * @param address Connection address of this (IMAP/SMTP) service
  * @param created Time when this email service was added to the database
  * @param name Name of this email service. Empty if not defined.
  * @author Mikko Hilpinen
  * @since 22.12.2024, v1.1
  */
case class EmailServiceData(address: String, created: Instant = Now, name: String = "") 
	extends EmailServiceFactory[EmailServiceData] with ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("address" -> address, "created" -> created, "name" -> name))
	
	override def withAddress(address: String) = copy(address = address)
	override def withCreated(created: Instant) = copy(created = created)
	override def withName(name: String) = copy(name = name)
}

