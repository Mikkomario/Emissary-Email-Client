package vf.emissary.model.stored.messaging

import utopia.vault.model.template.StoredModelConvertible
import vf.emissary.database.access.single.messaging.address_name.DbSingleAddressName
import vf.emissary.model.partial.messaging.AddressNameData

/**
  * Represents a address name that has already been stored in the database
  * @param id id of this address name in the database
  * @param data Wrapped address name data
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
case class AddressName(id: Int, data: AddressNameData) extends StoredModelConvertible[AddressNameData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this address name in the database
	  */
	def access = DbSingleAddressName(id)
	
	/**
	 * @return Copy of this address name marked as self-assigned
	 */
	def selfAssigned = if (data.isSelfAssigned) this else copy(data = data.selfAssigned)
	
	
	// IMPLEMENTED  -----------------
	
	override def toString = data.name
}

