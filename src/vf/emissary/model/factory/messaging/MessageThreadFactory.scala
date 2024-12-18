package vf.emissary.model.factory.messaging

import java.time.Instant

/**
  * Common trait for message thread-related factories which allow construction with individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait MessageThreadFactory[+A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param created New created to assign
	  * @return Copy of this item with the specified created
	  */
	def withCreated(created: Instant): A
}

