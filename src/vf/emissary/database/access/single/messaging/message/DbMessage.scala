package vf.emissary.database.access.single.messaging.message

import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{SubView, UnconditionalView, View}
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.MessageFactory
import vf.emissary.database.model.messaging.MessageModel
import vf.emissary.model.stored.messaging.Message

import java.time.Instant

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
	 * Accesses a specific message in the DB
	 * @param threadId Id of the associated message thread
	 * @param messageId Identifier of the targeted message
	 * @param senderId Id of the sender of this message
	 * @param sendTime Time when this message was sent
	 * @return Access to that specific message
	 */
	def apply(threadId: Int, messageId: String, senderId: Int, sendTime: Instant) =
		new DbSpecificMessage(threadId, messageId, senderId, sendTime)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique messages.
	  * @return An access point to the message that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = UniqueMessageAccess(mergeCondition(condition))
	
	
	// NESTED   ------------------------
	
	class DbSpecificMessage(threadId: Int, messageId: String, senderId: Int, sendTime: Instant)
		extends UniqueMessageAccess with SubView
	{
		// ATTRIBUTES   ---------------
		
		private lazy val conditionModel = model.withThreadId(threadId).withMessageId(messageId)
			.withSenderId(senderId).withCreated(sendTime)
		
		override lazy val filterCondition: Condition = {
			val base = conditionModel.toCondition
			// Adds message_id IS NULL condition, if appropriate
			if (messageId.isEmpty) base && model.messageIdColumn.isNull else base
		}
		
		
		// IMPLEMENTED  ---------------
		
		override protected def parent: View = DbMessage
		
		
		// OTHER    -------------------
		
		/**
		 * Retrieves the id of this message. Inserts a new message if not already present in the DB.
		 * @param replyRefId Id of the message this message replies to.
		 *                   None if this message is not a reply.
		 *                   Call-by-name; Only called on insert.
		 * @param connection Implicit DB connection
		 * @return Either an existing message id (right), or the newly inserted message's id (left)
		 */
		def pullOrInsertId(replyRefId: => Option[Int] = None)(implicit connection: Connection) =
			id.toRight {
				// Applies the correct reply id
				val finalModel = replyRefId match {
					case Some(id) => conditionModel.withReplyToId(id)
					case None => conditionModel
				}
				finalModel.insert().getInt
			}
	}
}

