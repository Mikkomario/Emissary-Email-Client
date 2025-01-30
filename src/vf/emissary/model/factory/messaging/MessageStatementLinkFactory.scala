package vf.emissary.model.factory.messaging

import utopia.logos.model.factory.text.StatementPlacementFactory

/**
 * Common trait for message statement link-related factories which allow construction with individual properties
 *
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait MessageStatementLinkFactory[+A] extends StatementPlacementFactory[A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param messageId New message id to assign
	  * @return Copy of this item with the specified message id
	  */
	def withMessageId(messageId: Int): A
	
	
	// IMPLEMENTED	--------------------
	
	override def withParentId(parentId: Int) = withMessageId(parentId)
}

