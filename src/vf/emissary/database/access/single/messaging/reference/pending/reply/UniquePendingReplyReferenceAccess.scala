package vf.emissary.database.access.single.messaging.reference.pending.reply

import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{FilterableView, ViewFactory}
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.PendingReplyReferenceDbFactory
import vf.emissary.database.storable.messaging.PendingReplyReferenceDbModel
import vf.emissary.model.stored.messaging.PendingReplyReference

object UniquePendingReplyReferenceAccess extends ViewFactory[UniquePendingReplyReferenceAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): UniquePendingReplyReferenceAccess = 
		_UniquePendingReplyReferenceAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _UniquePendingReplyReferenceAccess(override val accessCondition: Option[Condition]) 
		extends UniquePendingReplyReferenceAccess
}

/**
  * A common trait for access points that return individual and distinct pending reply references.
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait UniquePendingReplyReferenceAccess 
	extends SingleRowModelAccess[PendingReplyReference] 
		with DistinctModelAccess[PendingReplyReference, Option[PendingReplyReference], Value] 
		with FilterableView[UniquePendingReplyReferenceAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the message from which this reference is made from. 
	  * None if no pending reply reference (or value) was found.
	  */
	def messageId(implicit connection: Connection) = pullColumn(model.messageId.column).int
	
	/**
	  * Message id of the referenced message. 
	  * None if no pending reply reference (or value) was found.
	  */
	def referencedMessageId(implicit connection: Connection) = 
		pullColumn(model.referencedMessageId.column).getString
	
	/**
	  * Time when this pending reply reference was added to the database. 
	  * None if no pending reply reference (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.created.column).instant
	
	/**
	  * 
		Unique id of the accessible pending reply reference. None if no pending reply reference was accessible.
	  */
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = PendingReplyReferenceDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = PendingReplyReferenceDbFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): UniquePendingReplyReferenceAccess = 
		UniquePendingReplyReferenceAccess(condition)
}

