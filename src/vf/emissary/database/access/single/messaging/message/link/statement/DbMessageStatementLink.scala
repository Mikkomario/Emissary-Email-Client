package vf.emissary.database.access.single.messaging.message.link.statement

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.MessageStatementLinkDbFactory
import vf.emissary.database.storable.messaging.MessageStatementLinkDbModel
import vf.emissary.model.stored.messaging.MessageStatementLink

/**
  * Used for accessing individual message statement links
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object DbMessageStatementLink 
	extends SingleRowModelAccess[MessageStatementLink] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	private def model = MessageStatementLinkDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageStatementLinkDbFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted message statement link
	  * @return An access point to that message statement link
	  */
	def apply(id: Int) = DbSingleMessageStatementLink(id)
	
	/**
	  * @param
	  * 
		 condition Filter condition to apply in addition to this root view's condition. Should yield unique message statement links.
	  * @return An access point to the message statement link that satisfies the specified condition
	  */
	private def distinct(condition: Condition) = UniqueMessageStatementLinkAccess(condition)
}

