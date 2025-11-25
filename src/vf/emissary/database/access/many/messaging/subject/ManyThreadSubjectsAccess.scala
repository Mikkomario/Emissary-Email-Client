package vf.emissary.database.access.many.messaging.subject

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.ThreadSubjectDbFactory
import vf.emissary.database.storable.messaging.MessageThreadSubjectLinkDbModel
import vf.emissary.model.combined.messaging.ThreadSubject

object ManyThreadSubjectsAccess extends ViewFactory[ManyThreadSubjectsAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyThreadSubjectsAccess = 
		_ManyThreadSubjectsAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyThreadSubjectsAccess(override val accessCondition: Option[Condition]) 
		extends ManyThreadSubjectsAccess
}

/**
  * A common trait for access points that return multiple thread subjects at a time
  * @author Mikko Hilpinen
  * @since 17.10.2023
  */
trait ManyThreadSubjectsAccess 
	extends ManySubjectsAccessLike[ThreadSubject, ManyThreadSubjectsAccess] with ManyRowModelAccess[ThreadSubject]
{
	// COMPUTED	--------------------
	
	/**
	  * thread ids of the accessible message thread subject links
	  */
	def threadLinkThreadIds(implicit connection: Connection) = 
		pullColumn(threadLinkModel.threadId.column).map { v => v.getInt }
	/**
	  * subject ids of the accessible message thread subject links
	  */
	def threadLinkSubjectIds(implicit connection: Connection) = 
		pullColumn(threadLinkModel.subjectId.column).map { v => v.getInt }
	/**
	  * creation times of the accessible message thread subject links
	  */
	def threadLinkCreationTimes(implicit connection: Connection) = 
		pullColumn(threadLinkModel.created.column).map { v => v.getInstant }
	
	/**
	  * Model (factory) used for interacting the message thread subject links associated 
		with this thread subject
	  */
	protected def threadLinkModel = MessageThreadSubjectLinkDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = ThreadSubjectDbFactory
	override def self = this
	
	override def apply(condition: Condition): ManyThreadSubjectsAccess = ManyThreadSubjectsAccess(condition)
	
	
	// OTHER	--------------------
	
	/**
	  * @param threadIds Ids of targeted threads
	  * @return Access to subjects used within those threads
	  */
	def inThreads(threadIds: Iterable[Int]) = filter(threadLinkModel.threadId.in(threadIds))
}

