package vf.emissary.database.access.single.messaging.message

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.MessageFactory
import vf.emissary.database.model.messaging.MessageModel
import vf.emissary.model.stored.messaging.Message

/**
  * Used for accessing individual messages
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbMessage extends SingleRowModelAccess[Message] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = MessageModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted message
	  * @return An access point to that message
	  */
	def apply(id: Int) = DbSingleMessage(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique messages.
	  * @return An access point to the message that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = UniqueMessageAccess(mergeCondition(condition))
}

