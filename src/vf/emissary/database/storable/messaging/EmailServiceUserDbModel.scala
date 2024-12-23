package vf.emissary.database.storable.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{FromIdFactory, HasId, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.factory.messaging.EmailServiceUserFactory
import vf.emissary.model.partial.messaging.EmailServiceUserData
import vf.emissary.model.stored.messaging.EmailServiceUser

import java.time.Instant

/**
  * 
	Used for constructing EmailServiceUserDbModel instances and for inserting email service users to the database
  * @author Mikko Hilpinen
  * @since 22.12.2024, v1.1
  */
object EmailServiceUserDbModel 
	extends StorableFactory[EmailServiceUserDbModel, EmailServiceUser, EmailServiceUserData] 
		with FromIdFactory[Int, EmailServiceUserDbModel] with HasIdProperty 
		with EmailServiceUserFactory[EmailServiceUserDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with service ids
	  */
	lazy val serviceId = property("serviceId")
	
	/**
	  * Database property used for interacting with address ids
	  */
	lazy val addressId = property("addressId")
	
	/**
	  * Database property used for interacting with creation times
	  */
	lazy val created = property("created")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = EmissaryTables.emailServiceUser
	
	override def apply(data: EmailServiceUserData): EmailServiceUserDbModel = 
		apply(None, Some(data.serviceId), Some(data.addressId), Some(data.created))
	
	/**
	  * @param addressId Email address that represents this user
	  * @return A model containing only the specified address id
	  */
	override def withAddressId(addressId: Int) = apply(addressId = Some(addressId))
	
	/**
	  * @param created Time when this email service user was added to the database
	  * @return A model containing only the specified created
	  */
	override def withCreated(created: Instant) = apply(created = Some(created))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	/**
	  * @param serviceId Id of the used emailing service
	  * @return A model containing only the specified service id
	  */
	override def withServiceId(serviceId: Int) = apply(serviceId = Some(serviceId))
	
	override protected def complete(id: Value, data: EmailServiceUserData) = EmailServiceUser(id.getInt, data)
}

/**
  * Used for interacting with EmailServiceUsers in the database
  * @param id email service user database id
  * @author Mikko Hilpinen
  * @since 22.12.2024, v1.1
  */
case class EmailServiceUserDbModel(id: Option[Int] = None, serviceId: Option[Int] = None, 
	addressId: Option[Int] = None, created: Option[Instant] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, EmailServiceUserDbModel] 
		with EmailServiceUserFactory[EmailServiceUserDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties = 
		Vector(EmailServiceUserDbModel.id.name -> id, EmailServiceUserDbModel.serviceId.name -> serviceId, 
			EmailServiceUserDbModel.addressId.name -> addressId, 
			EmailServiceUserDbModel.created.name -> created)
	
	
	// IMPLEMENTED	--------------------
	
	override def table = EmailServiceUserDbModel.table
	
	/**
	  * @param addressId Email address that represents this user
	  * @return A new copy of this model with the specified address id
	  */
	override def withAddressId(addressId: Int) = copy(addressId = Some(addressId))
	
	/**
	  * @param created Time when this email service user was added to the database
	  * @return A new copy of this model with the specified created
	  */
	override def withCreated(created: Instant) = copy(created = Some(created))
	
	override def withId(id: Int) = copy(id = Some(id))
	
	/**
	  * @param serviceId Id of the used emailing service
	  * @return A new copy of this model with the specified service id
	  */
	override def withServiceId(serviceId: Int) = copy(serviceId = Some(serviceId))
}

