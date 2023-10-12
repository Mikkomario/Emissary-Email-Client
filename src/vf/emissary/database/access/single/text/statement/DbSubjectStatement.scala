package vf.emissary.database.access.single.text.statement

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.text.SubjectStatementFactory
import vf.emissary.database.model.messaging.SubjectStatementLinkModel
import vf.emissary.database.model.text.StatementModel
import vf.emissary.model.combined.text.SubjectStatement

/**
  * Used for accessing individual subject statements
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbSubjectStatement extends SingleRowModelAccess[SubjectStatement] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * A database model (factory) used for interacting with linked statements
	  */
	protected def model = StatementModel
	
	/**
	  * A database model (factory) used for interacting with the linked subject link
	  */
	protected def subjectLinkModel = SubjectStatementLinkModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = SubjectStatementFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted subject statement
	  * @return An access point to that subject statement
	  */
	def apply(id: Int) = DbSingleSubjectStatement(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique subject statements.
	  * @return An access point to the subject statement that satisfies the specified condition
	  */
	protected
		 def filterDistinct(condition: Condition) = UniqueSubjectStatementAccess(mergeCondition(condition))
}

