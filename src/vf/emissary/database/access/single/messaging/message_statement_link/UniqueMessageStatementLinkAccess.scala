package vf.emissary.database.access.single.messaging.message_statement_link

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.MessageStatementLinkFactory
import vf.emissary.database.model.messaging.MessageStatementLinkModel
import vf.emissary.model.stored.messaging.MessageStatementLink

object UniqueMessageStatementLinkAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueMessageStatementLinkAccess = 
		new _UniqueMessageStatementLinkAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueMessageStatementLinkAccess(condition: Condition)
		 extends UniqueMessageStatementLinkAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct message statement links.
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait UniqueMessageStatementLinkAccess 
	extends SingleRowModelAccess[MessageStatementLink] with FilterableView[UniqueMessageStatementLinkAccess] 
		with DistinctModelAccess[MessageStatementLink, Option[MessageStatementLink], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * 
		Id of the message where the statement was made. None if no message statement link (or value) was found.
	  */
	def messageId(implicit connection: Connection) = pullColumn(model.messageIdColumn).int
	
	/**
	  * The statement that was made. None if no message statement link (or value) was found.
	  */
	def statementId(implicit connection: Connection) = pullColumn(model.statementIdColumn).int
	
	/**
	  * 
		Index of the statement in the message (0-based). None if no message statement link (or value) was found.
	  */
	def orderIndex(implicit connection: Connection) = pullColumn(model.orderIndexColumn).int
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = MessageStatementLinkModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageStatementLinkFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueMessageStatementLinkAccess = 
		new UniqueMessageStatementLinkAccess._UniqueMessageStatementLinkAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the message ids of the targeted message statement links
	  * @param newMessageId A new message id to assign
	  * @return Whether any message statement link was affected
	  */
	def messageId_=(newMessageId: Int)(implicit connection: Connection) = 
		putColumn(model.messageIdColumn, newMessageId)
	
	/**
	  * Updates the order indexs of the targeted message statement links
	  * @param newOrderIndex A new order index to assign
	  * @return Whether any message statement link was affected
	  */
	def orderIndex_=(newOrderIndex: Int)(implicit connection: Connection) = 
		putColumn(model.orderIndexColumn, newOrderIndex)
	
	/**
	  * Updates the statement ids of the targeted message statement links
	  * @param newStatementId A new statement id to assign
	  * @return Whether any message statement link was affected
	  */
	def statementId_=(newStatementId: Int)(implicit connection: Connection) = 
		putColumn(model.statementIdColumn, newStatementId)
}

