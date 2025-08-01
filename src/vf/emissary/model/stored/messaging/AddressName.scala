package vf.emissary.model.stored.messaging

import utopia.flow.generic.model.template.ModelLike.AnyModel
import utopia.vault.store.{FromIdFactory, StoredFromModelFactory, StoredModelConvertible}
import vf.emissary.database.access.single.messaging.address.name.DbSingleAddressName
import vf.emissary.model.factory.messaging.AddressNameFactoryWrapper
import vf.emissary.model.partial.messaging.AddressNameData

object AddressName extends StoredFromModelFactory[AddressNameData, AddressName]
{
	// IMPLEMENTED	--------------------
	
	override def dataFactory = AddressNameData
	
	override protected def complete(model: AnyModel, data: AddressNameData) = 
		model("id").tryInt.map { apply(_, data) }
}

/**
  * Represents a address name that has already been stored in the database
  * @param id id of this address name in the database
  * @param data Wrapped address name data
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
case class AddressName(id: Int, data: AddressNameData) 
	extends StoredModelConvertible[AddressNameData] with FromIdFactory[Int, AddressName] 
		with AddressNameFactoryWrapper[AddressNameData, AddressName]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this address name in the database
	  */
	def access = DbSingleAddressName(id)
	
	/**
	  * Copy of this address name marked as self-assigned
	  */
	def selfAssigned = if (data.isSelfAssigned) this else copy(data = data.selfAssigned)
	
	
	// IMPLEMENTED	--------------------
	
	override def toString = data.name
	
	override protected def wrappedFactory = data
	
	override def withId(id: Int) = copy(id = id)
	
	override protected def wrap(data: AddressNameData) = copy(data = data)
}

