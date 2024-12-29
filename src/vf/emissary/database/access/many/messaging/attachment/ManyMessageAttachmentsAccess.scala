package vf.emissary.database.access.many.messaging.attachment

import utopia.flow.collection.CollectionExtensions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.MessageAttachmentDbFactory
import vf.emissary.database.storable.messaging.AttachmentMessageLinkDbModel
import vf.emissary.model.combined.messaging.MessageAttachment

object ManyMessageAttachmentsAccess extends ViewFactory[ManyMessageAttachmentsAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyMessageAttachmentsAccess = 
		_ManyMessageAttachmentsAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyMessageAttachmentsAccess(override val accessCondition: Option[Condition]) 
		extends ManyMessageAttachmentsAccess
}

/**
  * A common trait for access points that return multiple message attachments at a time
  * @author Mikko Hilpinen
  * @since 27.12.2024
  */
trait ManyMessageAttachmentsAccess 
	extends ManyAttachmentsAccessLike[MessageAttachment, ManyMessageAttachmentsAccess] 
		with ManyRowModelAccess[MessageAttachment]
{
	// COMPUTED	--------------------
	
	/**
	  * attachment ids of the accessible attachment message links
	  */
	def linkAttachmentIds(implicit connection: Connection) = 
		pullColumn(linkModel.attachmentId.column).map { v => v.getInt }
	/**
	  * message ids of the accessible attachment message links
	  */
	def linkMessageIds(implicit connection: Connection) = 
		pullColumn(linkModel.messageId.column).map { v => v.getInt }
	
	/**
	  * Model (factory) used for interacting the attachment message links associated with this 
	  * message attachment
	  */
	protected def linkModel = AttachmentMessageLinkDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageAttachmentDbFactory
	override protected def self = this
	
	override def apply(condition: Condition): ManyMessageAttachmentsAccess = 
		ManyMessageAttachmentsAccess(condition)
		
	
	// OTHER    ------------------------
	
	/**
	 * @param messageIds Ids of the targeted messages
	 * @return Access to attachments linked to the specified messages
	 */
	def inMessages(messageIds: IterableOnce[Int]) =
		filter(linkModel.messageId.in(messageIds.toIntSet))
}

