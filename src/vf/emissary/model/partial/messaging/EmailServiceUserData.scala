package vf.emissary.model.partial.messaging

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now
import vf.emissary.model.factory.messaging.EmailServiceUserFactory

import java.time.Instant

object EmailServiceUserData extends FromModelFactoryWithSchema[EmailServiceUserData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("serviceId", IntType, Single("service_id")), 
			PropertyDeclaration("addressId", IntType, Single("address_id")), PropertyDeclaration("created", 
			InstantType, isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		EmailServiceUserData(valid("serviceId").getInt, valid("addressId").getInt, 
			valid("created").getInstant)
}

/**
  * Represents a user of a specific emailing service
  * @param serviceId Id of the used emailing service
  * @param addressId Email address that represents this user
  * @param created Time when this email service user was added to the database
  * @author Mikko Hilpinen
  * @since 22.12.2024, v1.1
  */
case class EmailServiceUserData(serviceId: Int, addressId: Int, created: Instant = Now) 
	extends EmailServiceUserFactory[EmailServiceUserData] with ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("serviceId" -> serviceId, "addressId" -> addressId, 
		"created" -> created))
	
	override def withAddressId(addressId: Int) = copy(addressId = addressId)
	
	override def withCreated(created: Instant) = copy(created = created)
	
	override def withServiceId(serviceId: Int) = copy(serviceId = serviceId)
}

