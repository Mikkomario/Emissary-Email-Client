package vf.emissary.database.access.many.messaging.subject_statement_link

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.SubjectStatementLinkFactory
import vf.emissary.database.model.messaging.SubjectStatementLinkModel
import vf.emissary.model.stored.messaging.SubjectStatementLink

object ManySubjectStatementLinksAccess
{
	// NESTED	--------------------
	
	private class ManySubjectStatementLinksSubView(condition: Condition)
		 extends ManySubjectStatementLinksAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple subject statement links at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait ManySubjectStatementLinksAccess 
	extends ManyRowModelAccess[SubjectStatementLink] with FilterableView[ManySubjectStatementLinksAccess] 
		with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * subject ids of the accessible subject statement links
	  */
	def subjectIds(implicit connection: Connection) = pullColumn(model.subjectIdColumn).map { v => v.getInt }
	
	/**
	  * statement ids of the accessible subject statement links
	  */
	def statementIds(implicit connection: Connection) = pullColumn(model.statementIdColumn)
		.map { v => v.getInt }
	
	/**
	  * order indexs of the accessible subject statement links
	  */
	def orderIndexs(implicit connection: Connection) = pullColumn(model.orderIndexColumn)
		.map { v => v.getInt }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = SubjectStatementLinkModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = SubjectStatementLinkFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManySubjectStatementLinksAccess = 
		new ManySubjectStatementLinksAccess.ManySubjectStatementLinksSubView(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the order indexs of the targeted subject statement links
	  * @param newOrderIndex A new order index to assign
	  * @return Whether any subject statement link was affected
	  */
	def orderIndexs_=(newOrderIndex: Int)(implicit connection: Connection) = 
		putColumn(model.orderIndexColumn, newOrderIndex)
	
	/**
	  * Updates the statement ids of the targeted subject statement links
	  * @param newStatementId A new statement id to assign
	  * @return Whether any subject statement link was affected
	  */
	def statementIds_=(newStatementId: Int)(implicit connection: Connection) = 
		putColumn(model.statementIdColumn, newStatementId)
	
	/**
	  * Updates the subject ids of the targeted subject statement links
	  * @param newSubjectId A new subject id to assign
	  * @return Whether any subject statement link was affected
	  */
	def subjectIds_=(newSubjectId: Int)(implicit connection: Connection) = 
		putColumn(model.subjectIdColumn, newSubjectId)
}

