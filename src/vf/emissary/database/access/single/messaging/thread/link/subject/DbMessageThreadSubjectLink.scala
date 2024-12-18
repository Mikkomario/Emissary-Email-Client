package vf.emissary.database.access.single.messaging.thread.link.subject

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.MessageThreadSubjectLinkDbFactory
import vf.emissary.database.storable.messaging.MessageThreadSubjectLinkDbModel
import vf.emissary.model.stored.messaging.MessageThreadSubjectLink

/**
  * Used for accessing individual message thread subject links
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object DbMessageThreadSubjectLink 
	extends SingleRowModelAccess[MessageThreadSubjectLink] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	private def model = MessageThreadSubjectLinkDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageThreadSubjectLinkDbFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted message thread subject link
	  * @return An access point to that message thread subject link
	  */
	def apply(id: Int) = DbSingleMessageThreadSubjectLink(id)
	
	/**
	  * @param
	  * 
		 condition Filter condition to apply in addition to this root view's condition. Should yield unique message thread subject links.
	  * @return An access point to the message thread subject link that satisfies the specified condition
	  */
	private def distinct(condition: Condition) = UniqueMessageThreadSubjectLinkAccess(condition)
}

