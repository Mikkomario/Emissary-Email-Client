package vf.emissary.model.factory.messaging

import java.time.Instant

/**
  * Common trait for pending reply reference-related factories which allow construction 
	with individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait PendingReplyReferenceFactory[+A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param created New created to assign
	  * @return Copy of this item with the specified created
	  */
	def withCreated(created: Instant): A
	
	/**
	  * @param messageId New message id to assign
	  * @return Copy of this item with the specified message id
	  */
	def withMessageId(messageId: Int): A
	
	/**
	  * @param referencedMessageId New referenced message id to assign
	  * @return Copy of this item with the specified referenced message id
	  */
	def withReferencedMessageId(referencedMessageId: String): A
}

