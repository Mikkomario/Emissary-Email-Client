package vf.emissary.database.model.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.emissary.database.factory.messaging.AddressFactory
import vf.emissary.model.partial.messaging.AddressData
import vf.emissary.model.stored.messaging.Address

import java.time.Instant

/**
  * Used for constructing AddressModel instances and for inserting addresses to the database
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object AddressModel extends DataInserter[AddressModel, Address, AddressData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains address address
	  */
	val addressAttName = "address"
	
	/**
	  * Name of the property that contains address name
	  */
	val nameAttName = "name"
	
	/**
	  * Name of the property that contains address created
	  */
	val createdAttName = "created"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains address address
	  */
	def addressColumn = table(addressAttName)
	
	/**
	  * Column that contains address name
	  */
	def nameColumn = table(nameAttName)
	
	/**
	  * Column that contains address created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = AddressFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: AddressData) = apply(None, data.address, data.name, Some(data.created))
	
	override protected def complete(id: Value, data: AddressData) = Address(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @return A model containing only the specified address
	  */
	def withAddress(address: String) = apply(address = address)
	
	/**
	  * @param created Time when this address was added to the database
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param id A address id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param name Human-readable name of this entity, if available
	  * @return A model containing only the specified name
	  */
	def withName(name: String) = apply(name = name)
}

/**
  * Used for interacting with Addresses in the database
  * @param id address database id
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class AddressModel(id: Option[Int] = None, address: String = "", name: String = "", 
	created: Option[Instant] = None) 
	extends StorableWithFactory[Address]
{
	// IMPLEMENTED	--------------------
	
	override def factory = AddressModel.factory
	
	override def valueProperties = {
		import AddressModel._
		Vector("id" -> id, addressAttName -> address, nameAttName -> name, createdAttName -> created)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @return A new copy of this model with the specified address
	  */
	def withAddress(address: String) = copy(address = address)
	
	/**
	  * @param created Time when this address was added to the database
	  * @return A new copy of this model with the specified created
	  */
	def withCreated(created: Instant) = copy(created = Some(created))
	
	/**
	  * @param name Human-readable name of this entity, if available
	  * @return A new copy of this model with the specified name
	  */
	def withName(name: String) = copy(name = name)
}

