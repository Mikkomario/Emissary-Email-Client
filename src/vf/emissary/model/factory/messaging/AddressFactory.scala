package vf.emissary.model.factory.messaging

import java.time.Instant

/**
  * Common trait for address-related factories which allow construction with individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait AddressFactory[+A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param address New address to assign
	  * @return Copy of this item with the specified address
	  */
	def withAddress(address: String): A
	
	/**
	  * @param created New created to assign
	  * @return Copy of this item with the specified created
	  */
	def withCreated(created: Instant): A
}

