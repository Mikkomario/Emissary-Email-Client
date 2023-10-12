package vf.emissary.database.access.single.text.statement

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.text.MessageStatementFactory
import vf.emissary.database.model.messaging.MessageStatementLinkModel
import vf.emissary.database.model.text.StatementModel
import vf.emissary.model.combined.text.MessageStatement

/**
  * Used for accessing individual message statements
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbMessageStatement extends SingleRowModelAccess[MessageStatement] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * A database model (factory) used for interacting with linked statements
	  */
	protected def model = StatementModel
	
	/**
	  * A database model (factory) used for interacting with the linked message link
	  */
	protected def messageLinkModel = MessageStatementLinkModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageStatementFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted message statement
	  * @return An access point to that message statement
	  */
	def apply(id: Int) = DbSingleMessageStatement(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique message statements.
	  * @return An access point to the message statement that satisfies the specified condition
	  */
	protected
		 def filterDistinct(condition: Condition) = UniqueMessageStatementAccess(mergeCondition(condition))
}

