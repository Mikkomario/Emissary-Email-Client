package vf.emissary.database.access.single.messaging.pending_reply_reference

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.PendingReplyReferenceFactory
import vf.emissary.database.model.messaging.PendingReplyReferenceModel
import vf.emissary.model.stored.messaging.PendingReplyReference

import java.time.Instant

object UniquePendingReplyReferenceAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniquePendingReplyReferenceAccess = 
		new _UniquePendingReplyReferenceAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniquePendingReplyReferenceAccess(condition: Condition) 
		extends UniquePendingReplyReferenceAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct pending reply references.
  * @author Mikko Hilpinen
  * @since 19.10.2023, v0.1
  */
trait UniquePendingReplyReferenceAccess 
	extends SingleRowModelAccess[PendingReplyReference] 
		with FilterableView[UniquePendingReplyReferenceAccess] 
		with DistinctModelAccess[PendingReplyReference, Option[PendingReplyReference], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * 
		Id of the message from which this reference is made from. None if no pending reply reference (or value)
	  *  was found.
	  */
	def messageId(implicit connection: Connection) = pullColumn(model.messageIdColumn).int
	
	/**
	  * Message id of the referenced message. None if no pending reply reference (or value) was found.
	  */
	def referencedMessageId(implicit connection: Connection) = 
		pullColumn(model.referencedMessageIdColumn).getString
	
	/**
	  * Time when this pending reply reference was added to the database. None if no pending
	  *  reply reference (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.createdColumn).instant
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = PendingReplyReferenceModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = PendingReplyReferenceFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniquePendingReplyReferenceAccess = 
		new UniquePendingReplyReferenceAccess._UniquePendingReplyReferenceAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the creation times of the targeted pending reply references
	  * @param newCreated A new created to assign
	  * @return Whether any pending reply reference was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the message ids of the targeted pending reply references
	  * @param newMessageId A new message id to assign
	  * @return Whether any pending reply reference was affected
	  */
	def messageId_=(newMessageId: Int)(implicit connection: Connection) = 
		putColumn(model.messageIdColumn, newMessageId)
	
	/**
	  * Updates the referenced message ids of the targeted pending reply references
	  * @param newReferencedMessageId A new referenced message id to assign
	  * @return Whether any pending reply reference was affected
	  */
	def referencedMessageId_=(newReferencedMessageId: String)(implicit connection: Connection) = 
		putColumn(model.referencedMessageIdColumn, newReferencedMessageId)
}

