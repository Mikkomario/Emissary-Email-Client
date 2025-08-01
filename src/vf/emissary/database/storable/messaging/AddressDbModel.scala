package vf.emissary.database.storable.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.HasIdProperty
import utopia.vault.store.{FromIdFactory, HasId}
import utopia.vault.nosql.storable.StorableFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.factory.messaging.AddressFactory
import vf.emissary.model.partial.messaging.AddressData
import vf.emissary.model.stored.messaging.Address

import java.time.Instant

/**
  * Used for constructing AddressDbModel instances and for inserting addresses to the database
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object AddressDbModel 
	extends StorableFactory[AddressDbModel, Address, AddressData] with FromIdFactory[Int, AddressDbModel] 
		with HasIdProperty with AddressFactory[AddressDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with addresses
	  */
	lazy val address = property("address")
	
	/**
	  * Database property used for interacting with creation times
	  */
	lazy val created = property("created")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = EmissaryTables.address
	
	override def apply(data: AddressData): AddressDbModel = apply(None, data.address, Some(data.created))
	
	/**
	  * @param address A string representation of this address
	  * @return A model containing only the specified address
	  */
	override def withAddress(address: String) = apply(address = address)
	
	/**
	  * @param created Time when this address was added to the database
	  * @return A model containing only the specified created
	  */
	override def withCreated(created: Instant) = apply(created = Some(created))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	override protected def complete(id: Value, data: AddressData) = Address(id.getInt, data)
}

/**
  * Used for interacting with Addresses in the database
  * @param id address database id
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
case class AddressDbModel(id: Option[Int] = None, address: String = "", created: Option[Instant] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, AddressDbModel] 
		with AddressFactory[AddressDbModel]
{
	// IMPLEMENTED	--------------------
	
	override def table = AddressDbModel.table
	
	override def valueProperties = 
		Vector(AddressDbModel.id.name -> id, AddressDbModel.address.name -> address, 
			AddressDbModel.created.name -> created)
	
	/**
	  * @param address A string representation of this address
	  * @return A new copy of this model with the specified address
	  */
	override def withAddress(address: String) = copy(address = address)
	
	/**
	  * @param created Time when this address was added to the database
	  * @return A new copy of this model with the specified created
	  */
	override def withCreated(created: Instant) = copy(created = Some(created))
	
	override def withId(id: Int) = copy(id = Some(id))
}

