package vf.emissary.database.access.single.text.statement

import utopia.logos.database.access.single.text.statement.UniqueStatementAccessLike
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.sql.Condition
import vf.emissary.database.factory.text.SubjectStatementDbFactory
import vf.emissary.database.storable.messaging.SubjectStatementLinkDbModel
import vf.emissary.model.combined.text.SubjectStatement

object UniqueSubjectStatementAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition):
		 UniqueSubjectStatementAccess =  new _UniqueSubjectStatementAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueSubjectStatementAccess(condition: Condition) extends UniqueSubjectStatementAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return distinct subject statements
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait UniqueSubjectStatementAccess 
	extends UniqueStatementAccessLike[SubjectStatement, UniqueSubjectStatementAccess]
		with SingleRowModelAccess[SubjectStatement]
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the described subject. None if no subject statement link (or value) was found.
	  */
	def subjectLinkSubjectId(implicit connection: Connection) =
		pullColumn(subjectLinkModel.subjectId).int
	/**
	  * Id of the statement made within the referenced subject. None if no subject statement link (or value)
	  * was found.
	  */
	def subjectLinkStatementId(implicit connection: Connection) = 
		pullColumn(subjectLinkModel.statementId).int
	/**
	  * Index where this statement appears within the referenced subject (0-based). None if no subject
	  * statement link (or value) was found.
	  */
	def subjectLinkOrderIndex(implicit connection: Connection) = pullColumn(subjectLinkModel.orderIndex).int
	/**
	  * A database model (factory) used for interacting with the linked subject link
	  */
	protected def subjectLinkModel = SubjectStatementLinkDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = SubjectStatementDbFactory
	override protected def self = this
	
	override def apply(condition: Condition): UniqueSubjectStatementAccess = 
		UniqueSubjectStatementAccess(condition)
}

