package vf.emissary.model.stored.messaging

import utopia.vault.model.template.StoredModelConvertible
import vf.emissary.database.access.single.messaging.address.DbSingleAddress
import vf.emissary.model.partial.messaging.AddressData

/**
  * Represents a address that has already been stored in the database
  * @param id id of this address in the database
  * @param data Wrapped address data
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class Address(id: Int, data: AddressData) extends StoredModelConvertible[AddressData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this address in the database
	  */
	def access = DbSingleAddress(id)
}

