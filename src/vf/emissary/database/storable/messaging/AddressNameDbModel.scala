package vf.emissary.database.storable.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.HasIdProperty
import utopia.vault.store.{FromIdFactory, HasId}
import utopia.vault.nosql.storable.StorableFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.factory.messaging.AddressNameFactory
import vf.emissary.model.partial.messaging.AddressNameData
import vf.emissary.model.stored.messaging.AddressName

import java.time.Instant

/**
  * Used for constructing AddressNameDbModel instances and for inserting address names to the database
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object AddressNameDbModel 
	extends StorableFactory[AddressNameDbModel, AddressName, AddressNameData] 
		with FromIdFactory[Int, AddressNameDbModel] with HasIdProperty 
		with AddressNameFactory[AddressNameDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with address ids
	  */
	lazy val addressId = property("addressId")
	
	/**
	  * Database property used for interacting with names
	  */
	lazy val name = property("name")
	
	/**
	  * Database property used for interacting with creation times
	  */
	lazy val created = property("created")
	
	/**
	  * Database property used for interacting with are self assigned
	  */
	lazy val isSelfAssigned = property("isSelfAssigned")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = EmissaryTables.addressName
	
	override def apply(data: AddressNameData): AddressNameDbModel = 
		apply(None, Some(data.addressId), data.name, Some(data.created), Some(data.isSelfAssigned))
	
	/**
	  * @param addressId Id of the address to which this name corresponds
	  * @return A model containing only the specified address id
	  */
	override def withAddressId(addressId: Int) = apply(addressId = Some(addressId))
	
	/**
	  * @param created Time when this link was first documented
	  * @return A model containing only the specified created
	  */
	override def withCreated(created: Instant) = apply(created = Some(created))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	/**
	  * @param isSelfAssigned Whether this name is used by this person themselves
	  * @return A model containing only the specified is self assigned
	  */
	override def withIsSelfAssigned(isSelfAssigned: Boolean) = apply(isSelfAssigned = Some(isSelfAssigned))
	
	/**
	  * @param name Human-readable name of this entity, if available
	  * @return A model containing only the specified name
	  */
	override def withName(name: String) = apply(name = name)
	
	override protected def complete(id: Value, data: AddressNameData) = AddressName(id.getInt, data)
}

/**
  * Used for interacting with AddressNames in the database
  * @param id address name database id
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
case class AddressNameDbModel(id: Option[Int] = None, addressId: Option[Int] = None, name: String = "", 
	created: Option[Instant] = None, isSelfAssigned: Option[Boolean] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, AddressNameDbModel] 
		with AddressNameFactory[AddressNameDbModel]
{
	// IMPLEMENTED	--------------------
	
	override def table = AddressNameDbModel.table
	
	override def valueProperties = 
		Vector(AddressNameDbModel.id.name -> id, AddressNameDbModel.addressId.name -> addressId, 
			AddressNameDbModel.name.name -> name, AddressNameDbModel.created.name -> created, 
			AddressNameDbModel.isSelfAssigned.name -> isSelfAssigned)
	
	/**
	  * @param addressId Id of the address to which this name corresponds
	  * @return A new copy of this model with the specified address id
	  */
	override def withAddressId(addressId: Int) = copy(addressId = Some(addressId))
	
	/**
	  * @param created Time when this link was first documented
	  * @return A new copy of this model with the specified created
	  */
	override def withCreated(created: Instant) = copy(created = Some(created))
	
	override def withId(id: Int) = copy(id = Some(id))
	
	/**
	  * @param isSelfAssigned Whether this name is used by this person themselves
	  * @return A new copy of this model with the specified is self assigned
	  */
	override def withIsSelfAssigned(isSelfAssigned: Boolean) = copy(isSelfAssigned = Some(isSelfAssigned))
	
	/**
	  * @param name Human-readable name of this entity, if available
	  * @return A new copy of this model with the specified name
	  */
	override def withName(name: String) = copy(name = name)
}

