package vf.emissary.database.access.single.messaging.message

import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{SubView, UnconditionalView, View}
import utopia.vault.sql.Condition
import utopia.vault.store.IdOrInserted
import vf.emissary.database.factory.messaging.MessageDbFactory
import vf.emissary.database.storable.messaging.MessageDbModel
import vf.emissary.model.partial.messaging.MessageData
import vf.emissary.model.stored.messaging.StoredMessage

import java.time.Instant

/**
  * Used for accessing individual messages
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbMessage extends SingleRowModelAccess[StoredMessage] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	private def model = MessageDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageDbFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted message
	  * @return An access point to that message
	  */
	def apply(id: Int) = DbSingleMessage(id)
	
	/**
	 * Targets a unique message
	 * @param threadId Id of the targeted thread
	 * @param messageId Targeted message ID (in the email system)
	 * @param senderId Id of the sender of this message
	 * @param sendTime Time when this message was sent
	 * @return Access to that message
	 */
	def apply(threadId: Int, messageId: String, senderId: Int, sendTime: Instant) =
		new DbSpecificMessage(threadId, messageId, senderId, sendTime)
	
	/**
	  * @param messageId Targeted message (string-based) id (may be empty)
	  * @param senderId Id of the message sender
	  * @param sendTime Time when the message was sent
	  * @return Access to a matching message in the databse
	  */
	def matching(messageId: String, senderId: Int, sendTime: Instant) = 
		filterDistinct(model.withMessageId(messageId).withSenderId(senderId).withCreated(sendTime).toCondition)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  * unique messages.
	  * @return An access point to the message that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = UniqueMessageAccess(mergeCondition(condition))
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield unique messages.
	  * @return An access point to the message that satisfies the specified condition
	  */
	private def distinct(condition: Condition) = UniqueMessageAccess(condition)
	
	
	// NESTED	--------------------
	
	class DbSpecificMessage(threadId: Int, messageId: String, senderId: Int, sendTime: Instant) 
		extends UniqueMessageAccess with SubView
	{
		// ATTRIBUTES	--------------------
		
		private lazy val conditionModel = 
			this.model.withThreadId(threadId).withMessageId(messageId).withSenderId(senderId).withCreated(sendTime)
		
		override lazy val filterCondition: Condition = {
			val base = conditionModel.toCondition
			// Adds message_id IS NULL condition, if appropriate
			if (messageId.isEmpty) base && this.model.messageId.isNull else base
		}
		
		
		// IMPLEMENTED	--------------------
		
		override protected def parent: View = DbMessage
		
		
		// OTHER	--------------------
		
		/**
		  * Retrieves the id of this message. Inserts a new message if not already present in the DB.
		  * @param replyRefId Id of the message this message replies to.
		  * None if this message is not a reply.
		  * Call-by-name; Only called on insert.
		  * @param connection Implicit DB connection
		  * @return ID of an existing message, or a newly inserted message
		  */
		def pullOrInsertId(replyRefId: => Option[Int] = None)
		                  (implicit connection: Connection): IdOrInserted[StoredMessage] =
			id.toRight {
				// Applies the correct reply id
				MessageDbModel.insert(MessageData(threadId, senderId, messageId, replyRefId))
			}
	}
}

