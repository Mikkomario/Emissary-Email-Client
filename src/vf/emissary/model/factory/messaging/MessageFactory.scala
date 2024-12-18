package vf.emissary.model.factory.messaging

import java.time.Instant

/**
  * Common trait for message-related factories which allow construction with individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait MessageFactory[+A]
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
	def withMessageId(messageId: String): A
	
	/**
	  * @param replyToId New reply to id to assign
	  * @return Copy of this item with the specified reply to id
	  */
	def withReplyToId(replyToId: Int): A
	
	/**
	  * @param senderId New sender id to assign
	  * @return Copy of this item with the specified sender id
	  */
	def withSenderId(senderId: Int): A
	
	/**
	  * @param threadId New thread id to assign
	  * @return Copy of this item with the specified thread id
	  */
	def withThreadId(threadId: Int): A
}

