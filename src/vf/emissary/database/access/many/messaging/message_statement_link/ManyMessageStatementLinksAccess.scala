package vf.emissary.database.access.many.messaging.message_statement_link

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.MessageStatementLinkFactory
import vf.emissary.database.model.messaging.MessageStatementLinkModel
import vf.emissary.model.stored.messaging.MessageStatementLink

object ManyMessageStatementLinksAccess
{
	// NESTED	--------------------
	
	private class ManyMessageStatementLinksSubView(condition: Condition)
		 extends ManyMessageStatementLinksAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple message statement links at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait ManyMessageStatementLinksAccess 
	extends ManyRowModelAccess[MessageStatementLink] with FilterableView[ManyMessageStatementLinksAccess] 
		with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * message ids of the accessible message statement links
	  */
	def messageIds(implicit connection: Connection) = pullColumn(model.messageIdColumn).map { v => v.getInt }
	
	/**
	  * statement ids of the accessible message statement links
	  */
	def statementIds(implicit connection: Connection) = pullColumn(model.statementIdColumn)
		.map { v => v.getInt }
	
	/**
	  * order indexs of the accessible message statement links
	  */
	def orderIndexs(implicit connection: Connection) = pullColumn(model.orderIndexColumn)
		.map { v => v.getInt }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = MessageStatementLinkModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageStatementLinkFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyMessageStatementLinksAccess = 
		new ManyMessageStatementLinksAccess.ManyMessageStatementLinksSubView(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the message ids of the targeted message statement links
	  * @param newMessageId A new message id to assign
	  * @return Whether any message statement link was affected
	  */
	def messageIds_=(newMessageId: Int)(implicit connection: Connection) = 
		putColumn(model.messageIdColumn, newMessageId)
	
	/**
	  * Updates the order indexs of the targeted message statement links
	  * @param newOrderIndex A new order index to assign
	  * @return Whether any message statement link was affected
	  */
	def orderIndexs_=(newOrderIndex: Int)(implicit connection: Connection) = 
		putColumn(model.orderIndexColumn, newOrderIndex)
	
	/**
	  * Updates the statement ids of the targeted message statement links
	  * @param newStatementId A new statement id to assign
	  * @return Whether any message statement link was affected
	  */
	def statementIds_=(newStatementId: Int)(implicit connection: Connection) = 
		putColumn(model.statementIdColumn, newStatementId)
}

