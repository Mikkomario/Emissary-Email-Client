package vf.emissary.database.access.single.messaging.attachment.link.message

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.AttachmentMessageLinkDbFactory
import vf.emissary.database.storable.messaging.AttachmentMessageLinkDbModel
import vf.emissary.model.stored.messaging.AttachmentMessageLink

/**
  * Used for accessing individual attachment message links
  * @author Mikko Hilpinen
  * @since 27.12.2024, v1.1
  */
object DbAttachmentMessageLink 
	extends SingleRowModelAccess[AttachmentMessageLink] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	private def model = AttachmentMessageLinkDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = AttachmentMessageLinkDbFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted attachment message link
	  * @return An access point to that attachment message link
	  */
	def apply(id: Int) = DbSingleAttachmentMessageLink(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should 
	  *                  yield unique attachment message links.
	  * @return An access point to the attachment message link that satisfies the specified condition
	  */
	private def distinct(condition: Condition) = UniqueAttachmentMessageLinkAccess(condition)
}

