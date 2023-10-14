package vf.emissary.database.access.many.messaging.subject

import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.SubjectFactory
import vf.emissary.database.model.messaging.SubjectStatementLinkModel
import vf.emissary.model.stored.messaging.Subject

object ManySubjectsAccess
{
	// NESTED	--------------------
	
	private class ManySubjectsSubView(condition: Condition) extends ManySubjectsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple subjects at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait ManySubjectsAccess 
	extends ManySubjectsAccessLike[Subject, ManySubjectsAccess] with ManyRowModelAccess[Subject]
{
	// COMPUTED ------------------------
	
	/**
	 * @return Model used for interacting with subject-statement links
	 */
	protected def statementLinkModel = SubjectStatementLinkModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = SubjectFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManySubjectsAccess = 
		new ManySubjectsAccess.ManySubjectsSubView(mergeCondition(filterCondition))
		
	
	// OTHER    -----------------------
	
	/**
	 * @param length Targeted (maximum) length
	 * @param connection Implicit DB connection
	 * @return Accessible subjects that are shorter than the specified length
	 */
	def findShorterThan(length: Int)(implicit connection: Connection) =
		findNotLinkedTo(statementLinkModel.table, Some(statementLinkModel.withOrderIndex(length).toCondition))
}

