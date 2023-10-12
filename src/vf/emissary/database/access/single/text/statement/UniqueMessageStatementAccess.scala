package vf.emissary.database.access.single.text.statement

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleChronoRowModelAccess
import utopia.vault.sql.Condition
import vf.emissary.database.factory.text.MessageStatementFactory
import vf.emissary.database.model.messaging.MessageStatementLinkModel
import vf.emissary.model.combined.text.MessageStatement

object UniqueMessageStatementAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueMessageStatementAccess =
		 new _UniqueMessageStatementAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueMessageStatementAccess(condition: Condition) extends UniqueMessageStatementAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return distinct message statements
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait UniqueMessageStatementAccess 
	extends UniqueStatementAccessLike[MessageStatement] 
		with SingleChronoRowModelAccess[MessageStatement, UniqueMessageStatementAccess]
{
	// COMPUTED	--------------------
	
	/**
	  * 
		Id of the message where the statement was made. None if no message statement link (or value) was found.
	  */
	def messageLinkMessageId(implicit connection: Connection) = pullColumn(messageLinkModel
		.messageIdColumn).int
	
	/**
	  * The statement that was made. None if no message statement link (or value) was found.
	  */
	def messageLinkStatementId(implicit connection: Connection) = 
		pullColumn(messageLinkModel.statementIdColumn).int
	
	/**
	  * 
		Index of the statement in the message (0-based). None if no message statement link (or value) was found.
	  */
	def messageLinkOrderIndex(implicit connection: Connection) = pullColumn(messageLinkModel
		.orderIndexColumn).int
	
	/**
	  * A database model (factory) used for interacting with the linked message link
	  */
	protected def messageLinkModel = MessageStatementLinkModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageStatementFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueMessageStatementAccess = 
		new UniqueMessageStatementAccess._UniqueMessageStatementAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the message ids of the targeted message statement links
	  * @param newMessageId A new message id to assign
	  * @return Whether any message statement link was affected
	  */
	def messageLinkMessageId_=(newMessageId: Int)(implicit connection: Connection) = 
		putColumn(messageLinkModel.messageIdColumn, newMessageId)
	
	/**
	  * Updates the order indexs of the targeted message statement links
	  * @param newOrderIndex A new order index to assign
	  * @return Whether any message statement link was affected
	  */
	def messageLinkOrderIndex_=(newOrderIndex: Int)(implicit connection: Connection) = 
		putColumn(messageLinkModel.orderIndexColumn, newOrderIndex)
	
	/**
	  * Updates the statement ids of the targeted message statement links
	  * @param newStatementId A new statement id to assign
	  * @return Whether any message statement link was affected
	  */
	def messageLinkStatementId_=(newStatementId: Int)(implicit connection: Connection) = 
		putColumn(messageLinkModel.statementIdColumn, newStatementId)
}

