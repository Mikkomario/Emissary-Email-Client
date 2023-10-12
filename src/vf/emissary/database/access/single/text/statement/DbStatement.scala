package vf.emissary.database.access.single.text.statement

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.text.StatementFactory
import vf.emissary.database.model.text.StatementModel
import vf.emissary.model.stored.text.Statement

/**
  * Used for accessing individual statements
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbStatement extends SingleRowModelAccess[Statement] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = StatementModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = StatementFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted statement
	  * @return An access point to that statement
	  */
	def apply(id: Int) = DbSingleStatement(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique statements.
	  * @return An access point to the statement that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = UniqueStatementAccess(mergeCondition(condition))
}

