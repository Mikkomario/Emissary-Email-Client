package vf.emissary.database.access.single.messaging.message_statement_link

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.MessageStatementLinkFactory
import vf.emissary.database.model.messaging.MessageStatementLinkModel
import vf.emissary.model.stored.messaging.MessageStatementLink

/**
  * Used for accessing individual message statement links
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbMessageStatementLink 
	extends SingleRowModelAccess[MessageStatementLink] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = MessageStatementLinkModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageStatementLinkFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted message statement link
	  * @return An access point to that message statement link
	  */
	def apply(id: Int) = DbSingleMessageStatementLink(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique message statement links.
	  * @return An access point to the message statement link that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = 
		UniqueMessageStatementLinkAccess(mergeCondition(condition))
}

