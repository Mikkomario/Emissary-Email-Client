package vf.emissary.database.access.single.text.statement

import utopia.logos.database.access.single.text.statement.UniquePlacedStatementAccessLike
import utopia.logos.database.props.text.TextPlacementDbProps
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.sql.Condition
import vf.emissary.database.factory.text.MessageStatementFactory
import vf.emissary.database.storable.messaging.MessageStatementLinkDbModel
import vf.emissary.model.combined.text.MessageStatement

object UniqueMessageStatementAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition):
		 UniqueMessageStatementAccess =  new _UniqueMessageStatementAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueMessageStatementAccess(override val condition: Condition) extends UniqueMessageStatementAccess
}

/**
  * A common trait for access points that return distinct message statements
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait UniqueMessageStatementAccess 
	extends UniquePlacedStatementAccessLike[MessageStatement, UniqueMessageStatementAccess]
		with SingleRowModelAccess[MessageStatement]
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the message where the statement was made. None if no message statement link (or value) was found.
	  */
	def messageLinkMessageId(implicit connection: Connection) =
		pullColumn(messageLinkModel.messageId).int
	/**
	  * The statement that was made. None if no message statement link (or value) was found.
	  */
	def messageLinkStatementId(implicit connection: Connection) = 
		pullColumn(messageLinkModel.statementId).int
	/**
	  * Index of the statement in the message (0-based). None if no message statement link (or value) was found.
	  */
	def messageLinkOrderIndex(implicit connection: Connection) =
		pullColumn(messageLinkModel.orderIndex).int
	
	/**
	  * A database model (factory) used for interacting with the linked message link
	  */
	protected def messageLinkModel = MessageStatementLinkDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageStatementFactory
	override protected def self = this
	override protected def placementModel: TextPlacementDbProps = messageLinkModel
	
	override def apply(condition: Condition): UniqueMessageStatementAccess =
		UniqueMessageStatementAccess(condition)
}

