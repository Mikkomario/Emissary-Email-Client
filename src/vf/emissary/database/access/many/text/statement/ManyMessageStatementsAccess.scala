package vf.emissary.database.access.many.text.statement

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.sql.Condition
import vf.emissary.database.factory.text.MessageStatementFactory
import vf.emissary.database.model.messaging.MessageStatementLinkModel
import vf.emissary.model.combined.text.MessageStatement

object ManyMessageStatementsAccess
{
	// NESTED	--------------------
	
	private class SubAccess(condition: Condition) extends ManyMessageStatementsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return multiple message statements at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023
  */
trait ManyMessageStatementsAccess 
	extends ManyStatementsAccessLike[MessageStatement, ManyMessageStatementsAccess] 
		with ManyRowModelAccess[MessageStatement]
{
	// COMPUTED	--------------------
	
	/**
	  * message ids of the accessible message statement links
	  */
	def messageLinkMessageIds(implicit connection: Connection) = 
		pullColumn(messageLinkModel.messageIdColumn).map { v => v.getInt }
	
	/**
	  * statement ids of the accessible message statement links
	  */
	def messageLinkStatementIds(implicit connection: Connection) = 
		pullColumn(messageLinkModel.statementIdColumn).map { v => v.getInt }
	
	/**
	  * order indexs of the accessible message statement links
	  */
	def messageLinkOrderIndices(implicit connection: Connection) =
		pullColumn(messageLinkModel.orderIndexColumn).map { v => v.getInt }
	
	/**
	  * Model (factory) used for interacting the message statement links associated 
		with this message statement
	  */
	protected def messageLinkModel = MessageStatementLinkModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageStatementFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyMessageStatementsAccess = 
		new ManyMessageStatementsAccess.SubAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	 * @param messageIds Ids of the targeted messages
	 * @return Access to statements made within the specified messages
	 */
	def inMessages(messageIds: Iterable[Int]) = filter(messageLinkModel.messageIdColumn.in(messageIds))
	
	/**
	  * Updates the message ids of the targeted message statement links
	  * @param newMessageId A new message id to assign
	  * @return Whether any message statement link was affected
	  */
	def messageLinkMessageIds_=(newMessageId: Int)(implicit connection: Connection) = 
		putColumn(messageLinkModel.messageIdColumn, newMessageId)
	
	/**
	  * Updates the order indexs of the targeted message statement links
	  * @param newOrderIndex A new order index to assign
	  * @return Whether any message statement link was affected
	  */
	def messageLinkOrderIndices_=(newOrderIndex: Int)(implicit connection: Connection) =
		putColumn(messageLinkModel.orderIndexColumn, newOrderIndex)
	
	/**
	  * Updates the statement ids of the targeted message statement links
	  * @param newStatementId A new statement id to assign
	  * @return Whether any message statement link was affected
	  */
	def messageLinkStatementIds_=(newStatementId: Int)(implicit connection: Connection) = 
		putColumn(messageLinkModel.statementIdColumn, newStatementId)
}

