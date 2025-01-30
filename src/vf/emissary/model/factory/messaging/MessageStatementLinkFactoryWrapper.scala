package vf.emissary.model.factory.messaging

import utopia.logos.model.factory.text.StatementPlacementFactoryWrapper

/**
  * Common trait for classes that implement MessageStatementLinkFactory by wrapping a MessageStatementLinkFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait MessageStatementLinkFactoryWrapper[A <: MessageStatementLinkFactory[A], +Repr] 
	extends MessageStatementLinkFactory[Repr] with StatementPlacementFactoryWrapper[A, Repr]
{
	// IMPLEMENTED	--------------------
	
	override def withMessageId(messageId: Int) = withParentId(messageId)
}

