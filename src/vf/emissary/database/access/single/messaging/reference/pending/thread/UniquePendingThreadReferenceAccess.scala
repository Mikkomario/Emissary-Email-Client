package vf.emissary.database.access.single.messaging.reference.pending.thread

import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{FilterableView, ViewFactory}
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.PendingThreadReferenceDbFactory
import vf.emissary.database.storable.messaging.PendingThreadReferenceDbModel
import vf.emissary.model.stored.messaging.PendingThreadReference

object UniquePendingThreadReferenceAccess extends ViewFactory[UniquePendingThreadReferenceAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): UniquePendingThreadReferenceAccess = 
		_UniquePendingThreadReferenceAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _UniquePendingThreadReferenceAccess(override val accessCondition: Option[Condition]) 
		extends UniquePendingThreadReferenceAccess
}

/**
  * A common trait for access points that return individual and distinct pending thread references.
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait UniquePendingThreadReferenceAccess 
	extends SingleRowModelAccess[PendingThreadReference] 
		with DistinctModelAccess[PendingThreadReference, Option[PendingThreadReference], Value] 
		with FilterableView[UniquePendingThreadReferenceAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the message thread with which the referenced message is linked to. 
	  * None if no pending thread reference (or value) was found.
	  */
	def threadId(implicit connection: Connection) = pullColumn(model.threadId.column).int
	
	/**
	  * Message id belonging to some unread message in the linked thread. 
	  * None if no pending thread reference (or value) was found.
	  */
	def referencedMessageId(implicit connection: Connection) = 
		pullColumn(model.referencedMessageId.column).getString
	
	/**
	  * Time when this pending thread reference was added to the database. 
	  * None if no pending thread reference (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.created.column).instant
	
	/**
	  * 
		Unique id of the accessible pending thread reference. None if no pending thread reference was accessible.
	  */
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = PendingThreadReferenceDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = PendingThreadReferenceDbFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): UniquePendingThreadReferenceAccess = 
		UniquePendingThreadReferenceAccess(condition)
}

