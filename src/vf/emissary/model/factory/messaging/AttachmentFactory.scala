package vf.emissary.model.factory.messaging

/**
  * Common trait for attachment-related factories which allow construction with individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait AttachmentFactory[+A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param fileName New file name to assign
	  * @return Copy of this item with the specified file name
	  */
	def withFileName(fileName: String): A
	
	/**
	  * @param messageId New message id to assign
	  * @return Copy of this item with the specified message id
	  */
	def withMessageId(messageId: Int): A
}

