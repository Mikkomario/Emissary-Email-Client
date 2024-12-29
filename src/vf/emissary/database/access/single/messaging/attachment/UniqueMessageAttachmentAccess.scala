package vf.emissary.database.access.single.messaging.attachment

import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.MessageAttachmentDbFactory
import vf.emissary.database.storable.messaging.AttachmentMessageLinkDbModel
import vf.emissary.model.combined.messaging.MessageAttachment

object UniqueMessageAttachmentAccess extends ViewFactory[UniqueMessageAttachmentAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): UniqueMessageAttachmentAccess = 
		_UniqueMessageAttachmentAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _UniqueMessageAttachmentAccess(override val accessCondition: Option[Condition]) 
		extends UniqueMessageAttachmentAccess
}

/**
  * A common trait for access points that return distinct message attachments
  * @author Mikko Hilpinen
  * @since 27.12.2024, v1.1
  */
trait UniqueMessageAttachmentAccess 
	extends UniqueAttachmentAccessLike[MessageAttachment, UniqueMessageAttachmentAccess] 
		with SingleRowModelAccess[MessageAttachment]
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the linked attachment. 
	  * None if no attachment message link (or value) was found.
	  */
	def linkAttachmentId(implicit connection: Connection) = pullColumn(linkModel.attachmentId.column).int
	
	/**
	  * Id of the message in which the attachment appears. 
	  * None if no attachment message link (or value) was found.
	  */
	def linkMessageId(implicit connection: Connection) = pullColumn(linkModel.messageId.column).int
	
	/**
	  * A database model (factory) used for interacting with the linked link
	  */
	protected def linkModel = AttachmentMessageLinkDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageAttachmentDbFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): UniqueMessageAttachmentAccess = 
		UniqueMessageAttachmentAccess(condition)
}

