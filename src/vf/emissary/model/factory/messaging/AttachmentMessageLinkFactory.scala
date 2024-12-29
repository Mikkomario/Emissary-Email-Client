package vf.emissary.model.factory.messaging

/**
  * Common trait for attachment message link-related factories which allow construction with 
  * individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 27.12.2024, v1.1
  */
trait AttachmentMessageLinkFactory[+A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param attachmentId New attachment id to assign
	  * @return Copy of this item with the specified attachment id
	  */
	def withAttachmentId(attachmentId: Int): A
	
	/**
	  * @param messageId New message id to assign
	  * @return Copy of this item with the specified message id
	  */
	def withMessageId(messageId: Int): A
}

