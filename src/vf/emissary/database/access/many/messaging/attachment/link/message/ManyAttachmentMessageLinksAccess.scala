package vf.emissary.database.access.many.messaging.attachment.link.message

import utopia.flow.collection.immutable.IntSet
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{FilterableView, ViewFactory}
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.AttachmentMessageLinkDbFactory
import vf.emissary.database.storable.messaging.AttachmentMessageLinkDbModel
import vf.emissary.model.stored.messaging.AttachmentMessageLink

object ManyAttachmentMessageLinksAccess extends ViewFactory[ManyAttachmentMessageLinksAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyAttachmentMessageLinksAccess = 
		_ManyAttachmentMessageLinksAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyAttachmentMessageLinksAccess(override val accessCondition: Option[Condition]) 
		extends ManyAttachmentMessageLinksAccess
}

/**
  * A common trait for access points which target multiple attachment message links at a time
  * @author Mikko Hilpinen
  * @since 27.12.2024, v1.1
  */
trait ManyAttachmentMessageLinksAccess 
	extends ManyRowModelAccess[AttachmentMessageLink] with FilterableView[ManyAttachmentMessageLinksAccess] 
		with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * attachment ids of the accessible attachment message links
	  */
	def attachmentIds(implicit connection: Connection) = 
		pullColumn(model.attachmentId.column).map { v => v.getInt }
	
	/**
	  * message ids of the accessible attachment message links
	  */
	def messageIds(implicit connection: Connection) = pullColumn(model.messageId.column).map { v => v.getInt }
	
	/**
	  * Unique ids of the accessible attachment message links
	  */
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = AttachmentMessageLinkDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = AttachmentMessageLinkDbFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): ManyAttachmentMessageLinksAccess = 
		ManyAttachmentMessageLinksAccess(condition)
	
	
	// OTHER	--------------------
	
	/**
	  * @param messageId message id to target
	  * @return Copy of this access point that only includes attachment message links with the specified 
	  * message id
	  */
	def inMessage(messageId: Int) = filter(model.messageId.column <=> messageId)
	
	/**
	  * @param messageIds Targeted message ids
	  * @return Copy of this access point that only includes attachment message links where message id is 
	  * within the specified value set
	  */
	def inMessages(messageIds: IterableOnce[Int]) = filter(model.messageId.column.in(IntSet.from(messageIds)))
	
	/**
	  * @param attachmentId attachment id to target
	  * @return Copy of this access point that only includes attachment message links with the specified 
	  * attachment id
	  */
	def toAttachment(attachmentId: Int) = filter(model.attachmentId.column <=> attachmentId)
	
	/**
	  * @param attachmentIds Targeted attachment ids
	  * @return Copy of this access point that only includes attachment message links where attachment id is 
	  * within the specified value set
	  */
	def toAttachments(attachmentIds: IterableOnce[Int]) = 
		filter(model.attachmentId.column.in(IntSet.from(attachmentIds)))
}

