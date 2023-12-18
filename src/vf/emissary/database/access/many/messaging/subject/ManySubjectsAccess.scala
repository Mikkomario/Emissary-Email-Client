package vf.emissary.database.access.many.messaging.subject

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.SubjectFactory
import vf.emissary.database.model.messaging.{MessageThreadSubjectLinkModel, SubjectStatementLinkModel}
import vf.emissary.model.stored.messaging.Subject

object ManySubjectsAccess
{
	// NESTED	--------------------
	
	private class ManySubjectsSubView(condition: Condition) extends ManySubjectsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
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
	 * @return Model used for interacting with subject-thread links
	 */
	protected def threadLinkModel = MessageThreadSubjectLinkModel
	/**
	 * @return Model used for interacting with subject-statement links
	 */
	protected def statementLinkModel = SubjectStatementLinkModel
	
	/**
	 * @return Copy of this access point that includes message-thread links
	 */
	def threadSpecific = DbThreadSubjects.filter(accessCondition)
	
	
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
	
	/**
	 * Finds all accessible subjects that are used in the specified message threads
	 * @param threadIds Ids of targeted message threads
	 * @param connection Implicit DB Connection
	 * @return Accessible subjects mentioned in the specified threads
	 */
	def findInThreads(threadIds: Iterable[Int])(implicit connection: Connection) =
		find(threadLinkModel.threadIdColumn.in(threadIds), joins = Vector(threadLinkModel.table))
}

