package vf.emissary.database.access.single.messaging.message_recipient_link

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.MessageRecipientLinkFactory
import vf.emissary.database.model.messaging.MessageRecipientLinkModel
import vf.emissary.model.stored.messaging.MessageRecipientLink

/**
  * Used for accessing individual message recipient links
  * @author Mikko Hilpinen
  * @since 15.10.2023, v0.1
  */
object DbMessageRecipientLink 
	extends SingleRowModelAccess[MessageRecipientLink] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = MessageRecipientLinkModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageRecipientLinkFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted message recipient link
	  * @return An access point to that message recipient link
	  */
	def apply(id: Int) = DbSingleMessageRecipientLink(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique message recipient links.
	  * @return An access point to the message recipient link that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = 
		UniqueMessageRecipientLinkAccess(mergeCondition(condition))
}

