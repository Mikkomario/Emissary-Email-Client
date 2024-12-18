package vf.emissary.model.factory.messaging

import vf.emissary.model.enumeration.RecipientType

/**
  * Common trait for message recipient link-related factories which allow construction 
	with individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait MessageRecipientLinkFactory[+A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param messageId New message id to assign
	  * @return Copy of this item with the specified message id
	  */
	def withMessageId(messageId: Int): A
	
	/**
	  * @param recipientId New recipient id to assign
	  * @return Copy of this item with the specified recipient id
	  */
	def withRecipientId(recipientId: Int): A
	
	/**
	  * @param role New role to assign
	  * @return Copy of this item with the specified role
	  */
	def withRole(role: RecipientType): A
}

