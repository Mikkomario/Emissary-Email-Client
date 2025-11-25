package vf.emissary.database.access.many.messaging.attachment

import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.AttachmentDbFactory
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
	extends ManyAttachmentsAccessLike[Attachment, ManyAttachmentsAccess] with ManyRowModelAccess[Attachment]
{
	// COMPUTED ------------------------
	
	/**
	 * @return Copy of this access point which also includes attachment message links
	 */
	def messageLinked = DbMessageAttachments.filter(accessCondition)
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = AttachmentDbFactory
	override def self = this
	
	override def apply(condition: Condition): ManyAttachmentsAccess = ManyAttachmentsAccess(condition)
}

