package vf.emissary.model.stored.messaging

import utopia.flow.generic.model.template.ModelLike.AnyModel
import utopia.vault.store.{FromIdFactory, StoredFromModelFactory, StoredModelConvertible}
import vf.emissary.database.access.single.messaging.address.DbSingleAddress
import vf.emissary.model.factory.messaging.AddressFactoryWrapper
import vf.emissary.model.partial.messaging.AddressData

object Address extends StoredFromModelFactory[AddressData, Address]
{
	// IMPLEMENTED	--------------------
	
	override def dataFactory = AddressData
	
	override protected def complete(model: AnyModel, data: AddressData) = 
		model("id").tryInt.map { apply(_, data) }
}

/**
  * Represents a address that has already been stored in the database
  * @param id id of this address in the database
  * @param data Wrapped address data
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class Address(id: Int, data: AddressData) 
	extends StoredModelConvertible[AddressData] with FromIdFactory[Int, Address] 
		with AddressFactoryWrapper[AddressData, Address]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this address in the database
	  */
	def access = DbSingleAddress(id)
	
	
	// IMPLEMENTED	--------------------
	
	override def toString = data.address
	
	override protected def wrappedFactory = data
	
	override def withId(id: Int) = copy(id = id)
	
	override protected def wrap(data: AddressData) = copy(data = data)
}

