package vf.emissary.database.access.many.messaging.attachment

import utopia.flow.collection.immutable.IntSet
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{FilterableView, ViewFactory}
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.AttachmentDbFactory
import vf.emissary.database.storable.messaging.AttachmentDbModel
import vf.emissary.model.stored.messaging.Attachment

object ManyAttachmentsAccess extends ViewFactory[ManyAttachmentsAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyAttachmentsAccess = _ManyAttachmentsAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyAttachmentsAccess(override val accessCondition: Option[Condition]) 
		extends ManyAttachmentsAccess
}

/**
  * A common trait for access points which target multiple attachments at a time
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
trait ManyAttachmentsAccess 
	extends ManyRowModelAccess[Attachment] with FilterableView[ManyAttachmentsAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * message ids of the accessible attachments
	  */
	def messageIds(implicit connection: Connection) = pullColumn(model.messageId.column).map { v => v.getInt }
	/**
	  * file names of the accessible attachments
	  */
	def fileNames(implicit connection: Connection) = pullColumn(model.fileName.column).flatMap { _.string }
	/**
	  * Unique ids of the accessible attachments
	  */
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = AttachmentDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = AttachmentDbFactory
	override protected def self = this
	
	override def apply(condition: Condition): ManyAttachmentsAccess = ManyAttachmentsAccess(condition)
	
	
	// OTHER	--------------------
	
	/**
	 * @param messageId message id to target
	 * @return Copy of this access point that only includes attachments with the specified message id
	 */
	def withinMessage(messageId: Int) = filter(model.messageId.column <=> messageId)
	/**
	 * @param messageIds Targeted message ids
	 * @return Copy of this access point that only includes attachments where message id is within the specified value set
	 */
	def withinMessages(messageIds: IterableOnce[Int]) = filter(model
		.messageId.column.in(IntSet.from(messageIds)))
	
	/**
	  * @param messageId Id of the targeted message
	  * @return Access to attachments within that message
	  */
	@deprecated("Please use .withinMessage instead", "v1.1")
	def inMessage(messageId: Int) = filter(model.withMessageId(messageId).toCondition)
	/**
	  * @param messageIds Ids of the targeted messages
	  * @return Access to attachments in those messages
	  */
	@deprecated("Please use .withinMessages instead", "v1.1")
	def inMessages(messageIds: Iterable[Int]) = filter(model.messageId.in(messageIds))
}

