package vf.emissary.database.model.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.emissary.database.factory.messaging.MessageFactory
import vf.emissary.model.partial.messaging.MessageData
import vf.emissary.model.stored.messaging.Message

import java.time.Instant

/**
  * Used for constructing MessageModel instances and for inserting messages to the database
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object MessageModel extends DataInserter[MessageModel, Message, MessageData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains message thread id
	  */
	val threadIdAttName = "threadId"
	
	/**
	  * Name of the property that contains message sender id
	  */
	val senderIdAttName = "senderId"
	
	/**
	  * Name of the property that contains message message id
	  */
	val messageIdAttName = "messageId"
	
	/**
	  * Name of the property that contains message reply to id
	  */
	val replyToIdAttName = "replyToId"
	
	/**
	  * Name of the property that contains message created
	  */
	val createdAttName = "created"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains message thread id
	  */
	def threadIdColumn = table(threadIdAttName)
	
	/**
	  * Column that contains message sender id
	  */
	def senderIdColumn = table(senderIdAttName)
	
	/**
	  * Column that contains message message id
	  */
	def messageIdColumn = table(messageIdAttName)
	
	/**
	  * Column that contains message reply to id
	  */
	def replyToIdColumn = table(replyToIdAttName)
	
	/**
	  * Column that contains message created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = MessageFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: MessageData) = 
		apply(None, Some(data.threadId), Some(data.senderId), data.messageId, data.replyToId, 
			Some(data.created))
	
	override protected def complete(id: Value, data: MessageData) = Message(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param created Time when this message was sent
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param id A message id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param messageId (Unique) id given to this message by the sender
	  * @return A model containing only the specified message id
	  */
	def withMessageId(messageId: String) = apply(messageId = messageId)
	
	/**
	  * @param replyToId Id of the message this message replies to, if applicable
	  * @return A model containing only the specified reply to id
	  */
	def withReplyToId(replyToId: Int) = apply(replyToId = Some(replyToId))
	
	/**
	  * @param senderId Id of the address from which this message was sent
	  * @return A model containing only the specified sender id
	  */
	def withSenderId(senderId: Int) = apply(senderId = Some(senderId))
	
	/**
	  * @param threadId Id of the thread to which this message belongs
	  * @return A model containing only the specified thread id
	  */
	def withThreadId(threadId: Int) = apply(threadId = Some(threadId))
}

/**
  * Used for interacting with Messages in the database
  * @param id message database id
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class MessageModel(id: Option[Int] = None, threadId: Option[Int] = None, senderId: Option[Int] = None, 
	messageId: String = "", replyToId: Option[Int] = None, created: Option[Instant] = None) 
	extends StorableWithFactory[Message]
{
	// IMPLEMENTED	--------------------
	
	override def factory = MessageModel.factory
	
	override def valueProperties = {
		import MessageModel._
		Vector("id" -> id, threadIdAttName -> threadId, senderIdAttName -> senderId, 
			messageIdAttName -> messageId, replyToIdAttName -> replyToId, createdAttName -> created)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param created Time when this message was sent
	  * @return A new copy of this model with the specified created
	  */
	def withCreated(created: Instant) = copy(created = Some(created))
	
	/**
	  * @param messageId (Unique) id given to this message by the sender
	  * @return A new copy of this model with the specified message id
	  */
	def withMessageId(messageId: String) = copy(messageId = messageId)
	
	/**
	  * @param replyToId Id of the message this message replies to, if applicable
	  * @return A new copy of this model with the specified reply to id
	  */
	def withReplyToId(replyToId: Int) = copy(replyToId = Some(replyToId))
	
	/**
	  * @param senderId Id of the address from which this message was sent
	  * @return A new copy of this model with the specified sender id
	  */
	def withSenderId(senderId: Int) = copy(senderId = Some(senderId))
	
	/**
	  * @param threadId Id of the thread to which this message belongs
	  * @return A new copy of this model with the specified thread id
	  */
	def withThreadId(threadId: Int) = copy(threadId = Some(threadId))
}

