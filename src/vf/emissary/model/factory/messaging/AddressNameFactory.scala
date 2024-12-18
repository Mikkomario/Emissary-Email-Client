package vf.emissary.model.factory.messaging

import java.time.Instant

/**
  * Common trait for address name-related factories which allow construction with individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait AddressNameFactory[+A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param addressId New address id to assign
	  * @return Copy of this item with the specified address id
	  */
	def withAddressId(addressId: Int): A
	
	/**
	  * @param created New created to assign
	  * @return Copy of this item with the specified created
	  */
	def withCreated(created: Instant): A
	
	/**
	  * @param isSelfAssigned New is self assigned to assign
	  * @return Copy of this item with the specified is self assigned
	  */
	def withIsSelfAssigned(isSelfAssigned: Boolean): A
	
	/**
	  * @param name New name to assign
	  * @return Copy of this item with the specified name
	  */
	def withName(name: String): A
}

