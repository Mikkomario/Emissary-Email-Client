package vf.emissary.model.combined.text

import utopia.logos.model.combined.text.{CombinedStatement, PlacedStatementLike}
import utopia.logos.model.stored.text.StoredStatement
import vf.emissary.model.partial.messaging.MessageStatementLinkData
import vf.emissary.model.stored.messaging.MessageStatementLink

/**
  * Represents a statement made within a specific message context
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class MessageStatement(statement: StoredStatement, messageLink: MessageStatementLink)
	extends CombinedStatement[MessageStatement]
		with PlacedStatementLike[MessageStatement, MessageStatementLink, MessageStatementLinkData]
{
	override def placement = messageLink
	
	override protected def wrap(factory: StoredStatement): MessageStatement = copy(statement = factory)
}

