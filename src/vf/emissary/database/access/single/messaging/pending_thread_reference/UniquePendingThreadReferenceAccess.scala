package vf.emissary.database.access.single.messaging.pending_thread_reference

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.PendingThreadReferenceFactory
import vf.emissary.database.model.messaging.PendingThreadReferenceModel
import vf.emissary.model.stored.messaging.PendingThreadReference

import java.time.Instant

object UniquePendingThreadReferenceAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniquePendingThreadReferenceAccess = 
		new _UniquePendingThreadReferenceAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniquePendingThreadReferenceAccess(condition: Condition) 
		extends UniquePendingThreadReferenceAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct pending thread references.
  * @author Mikko Hilpinen
  * @since 19.10.2023, v0.1
  */
trait UniquePendingThreadReferenceAccess 
	extends SingleRowModelAccess[PendingThreadReference] 
		with FilterableView[UniquePendingThreadReferenceAccess] 
		with DistinctModelAccess[PendingThreadReference, Option[PendingThreadReference], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the message thread 
	  * 
		with which the referenced message is linked to. None if no pending thread reference (or value) was found.
	  */
	def threadId(implicit connection: Connection) = pullColumn(model.threadIdColumn).int
	
	/**
	  * Message id belonging to some unread message in the linked thread. None if no pending
	  *  thread reference (or value) was found.
	  */
	def referencedMessageId(implicit connection: Connection) = 
		pullColumn(model.referencedMessageIdColumn).getString
	
	/**
	  * Time when this pending thread reference was added to the database. None if no pending
	  *  thread reference (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.createdColumn).instant
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = PendingThreadReferenceModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = PendingThreadReferenceFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniquePendingThreadReferenceAccess = 
		new UniquePendingThreadReferenceAccess._UniquePendingThreadReferenceAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the creation times of the targeted pending thread references
	  * @param newCreated A new created to assign
	  * @return Whether any pending thread reference was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the referenced message ids of the targeted pending thread references
	  * @param newReferencedMessageId A new referenced message id to assign
	  * @return Whether any pending thread reference was affected
	  */
	def referencedMessageId_=(newReferencedMessageId: String)(implicit connection: Connection) = 
		putColumn(model.referencedMessageIdColumn, newReferencedMessageId)
	
	/**
	  * Updates the thread ids of the targeted pending thread references
	  * @param newThreadId A new thread id to assign
	  * @return Whether any pending thread reference was affected
	  */
	def threadId_=(newThreadId: Int)(implicit connection: Connection) = 
		putColumn(model.threadIdColumn, newThreadId)
}

