package vf.emissary.database.factory.messaging

import utopia.vault.nosql.factory.row.linked.CombiningFactory
import vf.emissary.model.combined.messaging.MessageAttachment
import vf.emissary.model.stored.messaging.{Attachment, AttachmentMessageLink}

/**
  * Used for reading message attachments from the database
  * @author Mikko Hilpinen
  * @since 27.12.2024, v1.1
  */
object MessageAttachmentDbFactory 
	extends CombiningFactory[MessageAttachment, Attachment, AttachmentMessageLink]
{
	// ATTRIBUTES	--------------------
	
	override val parentFactory = AttachmentDbFactory
	
	override val childFactory = AttachmentMessageLinkDbFactory
	
	
	// IMPLEMENTED	--------------------
	
	/**
	  * @param attachment attachment to wrap
	  * @param link       link to attach to this attachment
	  */
	override def apply(attachment: Attachment, link: AttachmentMessageLink) = MessageAttachment(attachment, 
		link)
}

