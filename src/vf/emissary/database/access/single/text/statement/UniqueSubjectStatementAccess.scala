package vf.emissary.database.access.single.text.statement

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleChronoRowModelAccess
import utopia.vault.sql.Condition
import vf.emissary.database.factory.text.SubjectStatementFactory
import vf.emissary.database.model.messaging.SubjectStatementLinkModel
import vf.emissary.model.combined.text.SubjectStatement

object UniqueSubjectStatementAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueSubjectStatementAccess =
		 new _UniqueSubjectStatementAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueSubjectStatementAccess(condition: Condition) extends UniqueSubjectStatementAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return distinct subject statements
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait UniqueSubjectStatementAccess 
	extends UniqueStatementAccessLike[SubjectStatement] 
		with SingleChronoRowModelAccess[SubjectStatement, UniqueSubjectStatementAccess]
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the described subject. None if no subject statement link (or value) was found.
	  */
	def subjectLinkSubjectId(implicit connection: Connection) = pullColumn(subjectLinkModel
		.subjectIdColumn).int
	
	/**
	  * Id of the statement made within the referenced subject. None if no subject statement link (or value)
	  *  was found.
	  */
	def subjectLinkStatementId(implicit connection: Connection) = 
		pullColumn(subjectLinkModel.statementIdColumn).int
	
	/**
	  * Index where this statement appears within the referenced subject (0-based). None if no subject
	  *  statement link (or value) was found.
	  */
	def subjectLinkOrderIndex(implicit connection: Connection) = pullColumn(subjectLinkModel
		.orderIndexColumn).int
	
	/**
	  * A database model (factory) used for interacting with the linked subject link
	  */
	protected def subjectLinkModel = SubjectStatementLinkModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = SubjectStatementFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueSubjectStatementAccess = 
		new UniqueSubjectStatementAccess._UniqueSubjectStatementAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the order indexs of the targeted subject statement links
	  * @param newOrderIndex A new order index to assign
	  * @return Whether any subject statement link was affected
	  */
	def subjectLinkOrderIndex_=(newOrderIndex: Int)(implicit connection: Connection) = 
		putColumn(subjectLinkModel.orderIndexColumn, newOrderIndex)
	
	/**
	  * Updates the statement ids of the targeted subject statement links
	  * @param newStatementId A new statement id to assign
	  * @return Whether any subject statement link was affected
	  */
	def subjectLinkStatementId_=(newStatementId: Int)(implicit connection: Connection) = 
		putColumn(subjectLinkModel.statementIdColumn, newStatementId)
	
	/**
	  * Updates the subject ids of the targeted subject statement links
	  * @param newSubjectId A new subject id to assign
	  * @return Whether any subject statement link was affected
	  */
	def subjectLinkSubjectId_=(newSubjectId: Int)(implicit connection: Connection) = 
		putColumn(subjectLinkModel.subjectIdColumn, newSubjectId)
}

