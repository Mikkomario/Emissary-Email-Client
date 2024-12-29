package vf.emissary.database.access.single.messaging.attachment

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.MessageAttachmentDbFactory
import vf.emissary.database.storable.messaging.{AttachmentDbModel, AttachmentMessageLinkDbModel}
import vf.emissary.model.combined.messaging.MessageAttachment

/**
  * Used for accessing individual message attachments
  * @author Mikko Hilpinen
  * @since 27.12.2024, v1.1
  */
object DbMessageAttachment extends SingleRowModelAccess[MessageAttachment] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * A database model (factory) used for interacting with the linked link
	  */
	protected def linkModel = AttachmentMessageLinkDbModel
	
	/**
	  * A database model (factory) used for interacting with linked attachments
	  */
	private def model = AttachmentDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageAttachmentDbFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted message attachment
	  * @return An access point to that message attachment
	  */
	def apply(id: Int) = DbSingleMessageAttachment(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should 
	  *                  yield unique message attachments.
	  * @return An access point to the message attachment that satisfies the specified condition
	  */
	private def distinct(condition: Condition) = UniqueMessageAttachmentAccess(condition)
}

