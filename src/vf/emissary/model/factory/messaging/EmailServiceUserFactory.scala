package vf.emissary.model.factory.messaging

import java.time.Instant

/**
  * Common trait for email service user-related factories which allow construction with individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 22.12.2024, v1.1
  */
trait EmailServiceUserFactory[+A]
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
	  * @param serviceId New service id to assign
	  * @return Copy of this item with the specified service id
	  */
	def withServiceId(serviceId: Int): A
}

