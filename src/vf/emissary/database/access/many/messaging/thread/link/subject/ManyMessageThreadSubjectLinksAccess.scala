package vf.emissary.database.access.many.messaging.thread.link.subject

import utopia.flow.collection.immutable.IntSet
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{ChronoRowFactoryView, ViewFactory}
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.MessageThreadSubjectLinkDbFactory
import vf.emissary.database.storable.messaging.MessageThreadSubjectLinkDbModel
import vf.emissary.model.stored.messaging.MessageThreadSubjectLink

object ManyMessageThreadSubjectLinksAccess extends ViewFactory[ManyMessageThreadSubjectLinksAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyMessageThreadSubjectLinksAccess = 
		_ManyMessageThreadSubjectLinksAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyMessageThreadSubjectLinksAccess(override val accessCondition: Option[Condition]) 
		extends ManyMessageThreadSubjectLinksAccess
}

/**
  * A common trait for access points which target multiple message thread subject links at a time
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait ManyMessageThreadSubjectLinksAccess 
	extends ManyRowModelAccess[MessageThreadSubjectLink] 
		with ChronoRowFactoryView[MessageThreadSubjectLink, ManyMessageThreadSubjectLinksAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * thread ids of the accessible message thread subject links
	  */
	def threadIds(implicit connection: Connection) = pullColumn(model.threadId.column).map { v => v.getInt }
	
	/**
	  * subject ids of the accessible message thread subject links
	  */
	def subjectIds(implicit connection: Connection) = pullColumn(model.subjectId.column).map { v => v.getInt }
	
	/**
	  * creation times of the accessible message thread subject links
	  */
	def creationTimes(implicit connection: Connection) = 
		pullColumn(model.created.column).map { v => v.getInstant }
	
	/**
	  * Unique ids of the accessible message thread subject links
	  */
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = MessageThreadSubjectLinkDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageThreadSubjectLinkDbFactory
	
	override def self = this
	
	override def apply(condition: Condition): ManyMessageThreadSubjectLinksAccess = 
		ManyMessageThreadSubjectLinksAccess(condition)
	
	
	// OTHER	--------------------
	
	/**
	  * @param threadId thread id to target
	  * @return Copy of this access point that only includes message thread subject links 
		with the specified thread id
	  */
	def inThread(threadId: Int) = filter(model.threadId.column <=> threadId)
	
	/**
	  * @param threadIds Targeted thread ids
	  * @return
	  * 
		 Copy of this access point that only includes message thread subject links where thread id is within the specified value set
	  */
	def inThreads(threadIds: IterableOnce[Int]) = filter(model.threadId.column.in(IntSet.from(threadIds)))
	
	/**
	  * @param subjectId subject id to target
	  * @return Copy of this access point that only includes message thread subject links 
	  * with the specified subject id
	  */
	def toSubject(subjectId: Int) = filter(model.subjectId.column <=> subjectId)
	
	/**
	  * @param subjectIds Targeted subject ids
	  * @return
	  * 
		 Copy of this access point that only includes message thread subject links where subject id is within the specified value set
	  */
	def toSubjects(subjectIds: IterableOnce[Int]) = filter(model.subjectId.column.in(IntSet.from(subjectIds)))
}

