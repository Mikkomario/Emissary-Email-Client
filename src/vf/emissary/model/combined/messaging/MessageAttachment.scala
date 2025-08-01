package vf.emissary.model.combined.messaging

import utopia.flow.view.template.Extender
import utopia.vault.store.HasId
import vf.emissary.model.factory.messaging.AttachmentFactoryWrapper
import vf.emissary.model.partial.messaging.AttachmentData
import vf.emissary.model.stored.messaging.{Attachment, AttachmentMessageLink}

object MessageAttachment
{
	// OTHER	--------------------
	
	/**
	  * @param attachment attachment to wrap
	  * @param link       link to attach to this attachment
	  * @return Combination of the specified attachment and link
	  */
	def apply(attachment: Attachment, link: AttachmentMessageLink): MessageAttachment = 
		_MessageAttachment(attachment, link)
	
	
	// NESTED	--------------------
	
	/**
	  * @param attachment attachment to wrap
	  * @param link       link to attach to this attachment
	  */
	private case class _MessageAttachment(attachment: Attachment, link: AttachmentMessageLink) 
		extends MessageAttachment
	{
		// IMPLEMENTED	--------------------
		
		override protected def wrap(factory: Attachment) = copy(attachment = factory)
	}
}

/**
  * Represents an attachment within a specific message
  * @author Mikko Hilpinen
  * @since 27.12.2024, v1.1
  */
trait MessageAttachment 
	extends Extender[AttachmentData] with HasId[Int] 
		with AttachmentFactoryWrapper[Attachment, MessageAttachment]
{
	// ABSTRACT	--------------------
	
	/**
	  * Wrapped attachment
	  */
	def attachment: Attachment
	
	/**
	  * The link that is attached to this attachment
	  */
	def link: AttachmentMessageLink
	
	
	// IMPLEMENTED	--------------------
	
	/**
	  * Id of this attachment in the database
	  */
	override def id = attachment.id
	
	override def wrapped = attachment.data
	
	override protected def wrappedFactory = attachment
}

