package vf.emissary.database.access.single.messaging.thread

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.MessageThreadDbFactory
import vf.emissary.database.storable.messaging.MessageThreadDbModel
import vf.emissary.model.stored.messaging.MessageThread

/**
  * Used for accessing individual message threads
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object DbMessageThread extends SingleRowModelAccess[MessageThread] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	private def model = MessageThreadDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageThreadDbFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted message thread
	  * @return An access point to that message thread
	  */
	def apply(id: Int) = DbSingleMessageThread(id)
	
	/**
	  * @param
	  * 
		 condition Filter condition to apply in addition to this root view's condition. Should yield unique message threads.
	  * @return An access point to the message thread that satisfies the specified condition
	  */
	private def distinct(condition: Condition) = UniqueMessageThreadAccess(condition)
}

