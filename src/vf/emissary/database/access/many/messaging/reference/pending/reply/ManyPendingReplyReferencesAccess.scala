package vf.emissary.database.access.many.messaging.reference.pending.reply

import utopia.flow.collection.immutable.IntSet
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{FilterableView, ViewFactory}
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.PendingReplyReferenceDbFactory
import vf.emissary.database.storable.messaging.PendingReplyReferenceDbModel
import vf.emissary.model.stored.messaging.PendingReplyReference

object ManyPendingReplyReferencesAccess extends ViewFactory[ManyPendingReplyReferencesAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyPendingReplyReferencesAccess = 
		_ManyPendingReplyReferencesAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyPendingReplyReferencesAccess(override val accessCondition: Option[Condition]) 
		extends ManyPendingReplyReferencesAccess
}

/**
  * A common trait for access points which target multiple pending reply references at a time
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait ManyPendingReplyReferencesAccess 
	extends ManyRowModelAccess[PendingReplyReference] with FilterableView[ManyPendingReplyReferencesAccess] 
		with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * message ids of the accessible pending reply references
	  */
	def messageIds(implicit connection: Connection) = pullColumn(model.messageId.column).map { v => v.getInt }
	
	/**
	  * referenced message ids of the accessible pending reply references
	  */
	def referencedMessageIds(implicit connection: Connection) = 
		pullColumn(model.referencedMessageId.column).flatMap { _.string }
	
	/**
	  * creation times of the accessible pending reply references
	  */
	def creationTimes(implicit connection: Connection) = 
		pullColumn(model.created.column).map { v => v.getInstant }
	
	/**
	  * Unique ids of the accessible pending reply references
	  */
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = PendingReplyReferenceDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = PendingReplyReferenceDbFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): ManyPendingReplyReferencesAccess = 
		ManyPendingReplyReferencesAccess(condition)
	
	
	// OTHER	--------------------
	
	/**
	  * @param messageId message id to target
	  * @return Copy of this access point that only includes pending reply references 
		with the specified message id
	  */
	def withinMessage(messageId: Int) = filter(model.messageId.column <=> messageId)
	
	/**
	  * @param messageIds Targeted message ids
	  * @return
	  * 
		 Copy of this access point that only includes pending reply references where message id is within the specified value set
	  */
	def withinMessages(messageIds: IterableOnce[Int]) = filter(model
		.messageId.column.in(IntSet.from(messageIds)))
}

