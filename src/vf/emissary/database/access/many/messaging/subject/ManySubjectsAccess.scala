package vf.emissary.database.access.many.messaging.subject

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.SubjectDbFactory
import vf.emissary.database.storable.messaging.{MessageThreadSubjectLinkDbModel, SubjectStatementLinkDbModel}
import vf.emissary.model.stored.messaging.Subject

object ManySubjectsAccess extends ViewFactory[ManySubjectsAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManySubjectsAccess = _ManySubjectsAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManySubjectsAccess(override val accessCondition: Option[Condition]) 
		extends ManySubjectsAccess
}

/**
  * A common trait for access points which target multiple subjects at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait ManySubjectsAccess 
	extends ManySubjectsAccessLike[Subject, ManySubjectsAccess] with ManyRowModelAccess[Subject]
{
	// COMPUTED	--------------------
	
	/**
	  * Copy of this access point that includes message-thread links
	  */
	def threadSpecific = DbThreadSubjects.filter(accessCondition)
	
	/**
	  * Model used for interacting with subject-thread links
	  */
	protected def threadLinkModel = MessageThreadSubjectLinkDbModel
	/**
	  * Model used for interacting with subject-statement links
	  */
	protected def statementLinkModel = SubjectStatementLinkDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = SubjectDbFactory
	
	override def self = this
	
	override def apply(condition: Condition): ManySubjectsAccess = ManySubjectsAccess(condition)
	
	
	// OTHER	--------------------
	
	/**
	  * Finds all accessible subjects that are used in the specified message threads
	  * @param threadIds Ids of targeted message threads
	  * @param connection Implicit DB Connection
	  * @return Accessible subjects mentioned in the specified threads
	  */
	def findInThreads(threadIds: Iterable[Int])(implicit connection: Connection) = 
		find(threadLinkModel.threadId.in(threadIds), joins = Vector(threadLinkModel.table))
	
	/**
	  * @param length Targeted (maximum) length
	  * @param connection Implicit DB connection
	  * @return Accessible subjects that are shorter than the specified length
	  */
	def findShorterThan(length: Int)(implicit connection: Connection) = 
		findNotLinkedTo(statementLinkModel.table, Some(statementLinkModel.withOrderIndex(length).toCondition))
}

