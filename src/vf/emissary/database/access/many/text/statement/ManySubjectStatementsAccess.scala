package vf.emissary.database.access.many.text.statement

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.sql.Condition
import vf.emissary.database.factory.text.SubjectStatementFactory
import vf.emissary.database.model.messaging.SubjectStatementLinkModel
import vf.emissary.model.combined.text.SubjectStatement

object ManySubjectStatementsAccess
{
	// NESTED	--------------------
	
	private class SubAccess(condition: Condition) extends ManySubjectStatementsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return multiple subject statements at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023
  */
trait ManySubjectStatementsAccess 
	extends ManyStatementsAccessLike[SubjectStatement, ManySubjectStatementsAccess] 
		with ManyRowModelAccess[SubjectStatement]
{
	// COMPUTED	--------------------
	
	/**
	  * subject ids of the accessible subject statement links
	  */
	def subjectLinkSubjectIds(implicit connection: Connection) = 
		pullColumn(subjectLinkModel.subjectIdColumn).map { v => v.getInt }
	
	/**
	  * statement ids of the accessible subject statement links
	  */
	def subjectLinkStatementIds(implicit connection: Connection) = 
		pullColumn(subjectLinkModel.statementIdColumn).map { v => v.getInt }
	
	/**
	  * order indexs of the accessible subject statement links
	  */
	def subjectLinkOrderIndices(implicit connection: Connection) =
		pullColumn(subjectLinkModel.orderIndexColumn).map { v => v.getInt }
	
	/**
	  * Model (factory) used for interacting the subject statement links associated 
		with this subject statement
	  */
	protected def subjectLinkModel = SubjectStatementLinkModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = SubjectStatementFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManySubjectStatementsAccess = 
		new ManySubjectStatementsAccess.SubAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	 * @param subjectIds Ids of targeted subjects
	 * @return Access to statements made within those subjects
	 */
	def inSubjects(subjectIds: Iterable[Int]) = filter(subjectLinkModel.subjectIdColumn.in(subjectIds))
	
	/**
	  * Updates the order indexs of the targeted subject statement links
	  * @param newOrderIndex A new order index to assign
	  * @return Whether any subject statement link was affected
	  */
	def subjectLinkOrderIndices_=(newOrderIndex: Int)(implicit connection: Connection) =
		putColumn(subjectLinkModel.orderIndexColumn, newOrderIndex)
	
	/**
	  * Updates the statement ids of the targeted subject statement links
	  * @param newStatementId A new statement id to assign
	  * @return Whether any subject statement link was affected
	  */
	def subjectLinkStatementIds_=(newStatementId: Int)(implicit connection: Connection) = 
		putColumn(subjectLinkModel.statementIdColumn, newStatementId)
	
	/**
	  * Updates the subject ids of the targeted subject statement links
	  * @param newSubjectId A new subject id to assign
	  * @return Whether any subject statement link was affected
	  */
	def subjectLinkSubjectIds_=(newSubjectId: Int)(implicit connection: Connection) = 
		putColumn(subjectLinkModel.subjectIdColumn, newSubjectId)
}

