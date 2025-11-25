package vf.emissary.database.access.many.messaging.reference.pending.thread

import utopia.flow.collection.immutable.IntSet
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{FilterableView, ViewFactory}
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.PendingThreadReferenceDbFactory
import vf.emissary.database.storable.messaging.PendingThreadReferenceDbModel
import vf.emissary.model.stored.messaging.PendingThreadReference

object ManyPendingThreadReferencesAccess extends ViewFactory[ManyPendingThreadReferencesAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyPendingThreadReferencesAccess = 
		_ManyPendingThreadReferencesAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyPendingThreadReferencesAccess(override val accessCondition: Option[Condition]) 
		extends ManyPendingThreadReferencesAccess
}

/**
  * A common trait for access points which target multiple pending thread references at a time
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait ManyPendingThreadReferencesAccess 
	extends ManyRowModelAccess[PendingThreadReference] with FilterableView[ManyPendingThreadReferencesAccess] 
		with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * thread ids of the accessible pending thread references
	  */
	def threadIds(implicit connection: Connection) = pullColumn(model.threadId.column).map { v => v.getInt }
	
	/**
	  * referenced message ids of the accessible pending thread references
	  */
	def referencedMessageIds(implicit connection: Connection) = 
		pullColumn(model.referencedMessageId.column).flatMap { _.string }
	
	/**
	  * creation times of the accessible pending thread references
	  */
	def creationTimes(implicit connection: Connection) = 
		pullColumn(model.created.column).map { v => v.getInstant }
	
	/**
	  * Unique ids of the accessible pending thread references
	  */
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = PendingThreadReferenceDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = PendingThreadReferenceDbFactory
	
	override def self = this
	
	override def apply(condition: Condition): ManyPendingThreadReferencesAccess = 
		ManyPendingThreadReferencesAccess(condition)
	
	
	// OTHER	--------------------
	
	/**
	  * @param threadId thread id to target
	  * @return Copy of this access point that only includes pending thread references 
		with the specified thread id
	  */
	def withinThread(threadId: Int) = filter(model.threadId.column <=> threadId)
	
	/**
	  * @param threadIds Targeted thread ids
	  * @return
	  * 
		 Copy of this access point that only includes pending thread references where thread id is within the specified value set
	  */
	def withinThreads(threadIds: IterableOnce[Int]) = filter(model.threadId.column.in(IntSet.from(threadIds)))
}

