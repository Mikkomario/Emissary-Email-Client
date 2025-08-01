package vf.emissary.database.storable.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.HasIdProperty
import utopia.vault.store.{FromIdFactory, HasId}
import utopia.vault.nosql.storable.StorableFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.factory.messaging.EmailServiceFactory
import vf.emissary.model.partial.messaging.EmailServiceData
import vf.emissary.model.stored.messaging.EmailService

import java.time.Instant

/**
  * Used for constructing EmailServiceDbModel instances and for inserting email services to the database
  * @author Mikko Hilpinen
  * @since 22.12.2024, v1.1
  */
object EmailServiceDbModel 
	extends StorableFactory[EmailServiceDbModel, EmailService, EmailServiceData] 
		with FromIdFactory[Int, EmailServiceDbModel] with HasIdProperty 
		with EmailServiceFactory[EmailServiceDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with addressses
	  */
	lazy val address = property("address")
	
	/**
	  * Database property used for interacting with creation times
	  */
	lazy val created = property("created")
	
	/**
	  * Database property used for interacting with names
	  */
	lazy val name = property("name")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = EmissaryTables.emailService
	
	override def apply(data: EmailServiceData): EmailServiceDbModel = 
		apply(None, data.address, Some(data.created), data.name)
	
	/**
	  * @param address Connection address of this (IMAP/SMTP) service
	  * @return A model containing only the specified address
	  */
	override def withAddress(address: String) = apply(address = address)
	
	/**
	  * @param created Time when this email service was added to the database
	  * @return A model containing only the specified created
	  */
	override def withCreated(created: Instant) = apply(created = Some(created))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	/**
	  * @param name Name of this email service. Empty if not defined.
	  * @return A model containing only the specified name
	  */
	override def withName(name: String) = apply(name = name)
	
	override protected def complete(id: Value, data: EmailServiceData) = EmailService(id.getInt, data)
}

/**
  * Used for interacting with EmailServices in the database
  * @param id email service database id
  * @author Mikko Hilpinen
  * @since 22.12.2024, v1.1
  */
case class EmailServiceDbModel(id: Option[Int] = None, address: String = "", created: Option[Instant] = None, 
	name: String = "") 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, EmailServiceDbModel] 
		with EmailServiceFactory[EmailServiceDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties = 
		Vector(EmailServiceDbModel.id.name -> id, EmailServiceDbModel.address.name -> address, 
			EmailServiceDbModel.created.name -> created, EmailServiceDbModel.name.name -> name)
	
	
	// IMPLEMENTED	--------------------
	
	override def table = EmailServiceDbModel.table
	
	/**
	  * @param address Connection address of this (IMAP/SMTP) service
	  * @return A new copy of this model with the specified address
	  */
	override def withAddress(address: String) = copy(address = address)
	
	/**
	  * @param created Time when this email service was added to the database
	  * @return A new copy of this model with the specified created
	  */
	override def withCreated(created: Instant) = copy(created = Some(created))
	
	override def withId(id: Int) = copy(id = Some(id))
	
	/**
	  * @param name Name of this email service. Empty if not defined.
	  * @return A new copy of this model with the specified name
	  */
	override def withName(name: String) = copy(name = name)
}

