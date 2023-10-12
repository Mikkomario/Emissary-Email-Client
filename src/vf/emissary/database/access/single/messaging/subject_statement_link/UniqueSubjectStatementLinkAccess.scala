package vf.emissary.database.access.single.messaging.subject_statement_link

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.SubjectStatementLinkFactory
import vf.emissary.database.model.messaging.SubjectStatementLinkModel
import vf.emissary.model.stored.messaging.SubjectStatementLink

object UniqueSubjectStatementLinkAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueSubjectStatementLinkAccess = 
		new _UniqueSubjectStatementLinkAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueSubjectStatementLinkAccess(condition: Condition)
		 extends UniqueSubjectStatementLinkAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct subject statement links.
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait UniqueSubjectStatementLinkAccess 
	extends SingleRowModelAccess[SubjectStatementLink] with FilterableView[UniqueSubjectStatementLinkAccess] 
		with DistinctModelAccess[SubjectStatementLink, Option[SubjectStatementLink], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the described subject. None if no subject statement link (or value) was found.
	  */
	def subjectId(implicit connection: Connection) = pullColumn(model.subjectIdColumn).int
	
	/**
	  * Id of the statement made within the referenced subject. None if no subject statement link (or value)
	  *  was found.
	  */
	def statementId(implicit connection: Connection) = pullColumn(model.statementIdColumn).int
	
	/**
	  * Index where this statement appears within the referenced subject (0-based). None if no subject
	  *  statement link (or value) was found.
	  */
	def orderIndex(implicit connection: Connection) = pullColumn(model.orderIndexColumn).int
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = SubjectStatementLinkModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = SubjectStatementLinkFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueSubjectStatementLinkAccess = 
		new UniqueSubjectStatementLinkAccess._UniqueSubjectStatementLinkAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the order indexs of the targeted subject statement links
	  * @param newOrderIndex A new order index to assign
	  * @return Whether any subject statement link was affected
	  */
	def orderIndex_=(newOrderIndex: Int)(implicit connection: Connection) = 
		putColumn(model.orderIndexColumn, newOrderIndex)
	
	/**
	  * Updates the statement ids of the targeted subject statement links
	  * @param newStatementId A new statement id to assign
	  * @return Whether any subject statement link was affected
	  */
	def statementId_=(newStatementId: Int)(implicit connection: Connection) = 
		putColumn(model.statementIdColumn, newStatementId)
	
	/**
	  * Updates the subject ids of the targeted subject statement links
	  * @param newSubjectId A new subject id to assign
	  * @return Whether any subject statement link was affected
	  */
	def subjectId_=(newSubjectId: Int)(implicit connection: Connection) = 
		putColumn(model.subjectIdColumn, newSubjectId)
}

