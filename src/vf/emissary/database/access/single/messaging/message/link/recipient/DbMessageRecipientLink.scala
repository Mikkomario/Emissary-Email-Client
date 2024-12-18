package vf.emissary.database.access.single.messaging.message.link.recipient

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.MessageRecipientLinkDbFactory
import vf.emissary.database.storable.messaging.MessageRecipientLinkDbModel
import vf.emissary.model.stored.messaging.MessageRecipientLink

/**
  * Used for accessing individual message recipient links
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object DbMessageRecipientLink 
	extends SingleRowModelAccess[MessageRecipientLink] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	private def model = MessageRecipientLinkDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageRecipientLinkDbFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted message recipient link
	  * @return An access point to that message recipient link
	  */
	def apply(id: Int) = DbSingleMessageRecipientLink(id)
	
	/**
	  * @param
	  * 
		 condition Filter condition to apply in addition to this root view's condition. Should yield unique message recipient links.
	  * @return An access point to the message recipient link that satisfies the specified condition
	  */
	private def distinct(condition: Condition) = UniqueMessageRecipientLinkAccess(condition)
}

