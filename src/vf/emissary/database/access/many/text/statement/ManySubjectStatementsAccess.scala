package vf.emissary.database.access.many.text.statement

import utopia.logos.database.access.many.text.statement.ManyPlacedStatementsAccessLike
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.emissary.database.factory.text.SubjectStatementFactory
import vf.emissary.database.storable.messaging.SubjectStatementLinkDbModel
import vf.emissary.model.combined.text.SubjectStatement

object ManySubjectStatementsAccess extends ViewFactory[ManySubjectStatementsAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManySubjectStatementsAccess = 
		_ManySubjectStatementsAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManySubjectStatementsAccess(override val accessCondition: Option[Condition]) 
		extends ManySubjectStatementsAccess
}

/**
  * A common trait for access points that return multiple subject statements at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023
  */
trait ManySubjectStatementsAccess 
	extends ManyPlacedStatementsAccessLike[SubjectStatement, ManySubjectStatementsAccess]
		with ManyRowModelAccess[SubjectStatement]
{
	// COMPUTED	--------------------
	
	/**
	  * subject ids of the accessible subject statement links
	  */
	def subjectLinkSubjectIds(implicit connection: Connection) = 
		pullColumn(subjectLinkModel.subjectId).map { v => v.getInt }
	/**
	  * statement ids of the accessible subject statement links
	  */
	def subjectLinkStatementIds(implicit connection: Connection) = 
		pullColumn(subjectLinkModel.statementId).map { v => v.getInt }
	/**
	  * order indexs of the accessible subject statement links
	  */
	def subjectLinkOrderIndices(implicit connection: Connection) = 
		pullColumn(subjectLinkModel.orderIndex).map { v => v.getInt }
		
	/**
	  * Model (factory) used for interacting the subject statement links associated 
	  * with this subject statement
	  */
	protected def subjectLinkModel = SubjectStatementLinkDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = SubjectStatementFactory
	override protected def self = this
	override protected def placementModel = subjectLinkModel
	
	
	// OTHER	--------------------
	
	def apply(condition: Condition): ManySubjectStatementsAccess = ManySubjectStatementsAccess(condition)
	
	/**
	  * @param subjectIds Ids of targeted subjects
	  * @return Access to statements made within those subjects
	  */
	def inSubjects(subjectIds: Iterable[Int]) = withinTexts(subjectIds)
}

