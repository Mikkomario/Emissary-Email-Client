package vf.emissary.database.storable.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{FromIdFactory, HasId, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.factory.messaging.MessageFactory
import vf.emissary.model.partial.messaging.MessageData
import vf.emissary.model.stored.messaging.StoredMessage

import java.time.Instant

/**
  * Used for constructing MessageDbModel instances and for inserting messages to the database
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object MessageDbModel 
	extends StorableFactory[MessageDbModel, StoredMessage, MessageData] with FromIdFactory[Int, MessageDbModel]
		with HasIdProperty with MessageFactory[MessageDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with thread ids
	  */
	lazy val threadId = property("threadId")
	
	/**
	  * Database property used for interacting with sender ids
	  */
	lazy val senderId = property("senderId")
	
	/**
	  * Database property used for interacting with message ids
	  */
	lazy val messageId = property("messageId")
	
	/**
	  * Database property used for interacting with reply to ids
	  */
	lazy val replyToId = property("replyToId")
	
	/**
	  * Database property used for interacting with creation times
	  */
	lazy val created = property("created")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = EmissaryTables.message
	
	override def apply(data: MessageData): MessageDbModel = 
		apply(None, Some(data.threadId), Some(data.senderId), data.messageId, data.replyToId, 
			Some(data.created))
	
	/**
	  * @param created Time when this message was sent
	  * @return A model containing only the specified created
	  */
	override def withCreated(created: Instant) = apply(created = Some(created))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	/**
	  * @param messageId (Unique) id given to this message by the sender
	  * @return A model containing only the specified message id
	  */
	override def withMessageId(messageId: String) = apply(messageId = messageId)
	
	/**
	  * @param replyToId Id of the message this message replies to, if applicable
	  * @return A model containing only the specified reply to id
	  */
	override def withReplyToId(replyToId: Int) = apply(replyToId = Some(replyToId))
	
	/**
	  * @param senderId Id of the address from which this message was sent
	  * @return A model containing only the specified sender id
	  */
	override def withSenderId(senderId: Int) = apply(senderId = Some(senderId))
	
	/**
	  * @param threadId Id of the thread to which this message belongs
	  * @return A model containing only the specified thread id
	  */
	override def withThreadId(threadId: Int) = apply(threadId = Some(threadId))
	
	override protected def complete(id: Value, data: MessageData) = StoredMessage(id.getInt, data)
}

/**
  * Used for interacting with Messages in the database
  * @param id message database id
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
case class MessageDbModel(id: Option[Int] = None, threadId: Option[Int] = None, senderId: Option[Int] = None, 
	messageId: String = "", replyToId: Option[Int] = None, created: Option[Instant] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, MessageDbModel] 
		with MessageFactory[MessageDbModel]
{
	// IMPLEMENTED	--------------------
	
	override def table = MessageDbModel.table
	
	override def valueProperties = 
		Vector(MessageDbModel.id.name -> id, MessageDbModel.threadId.name -> threadId, 
			MessageDbModel.senderId.name -> senderId, MessageDbModel.messageId.name -> messageId, 
			MessageDbModel.replyToId.name -> replyToId, MessageDbModel.created.name -> created)
	
	/**
	  * @param created Time when this message was sent
	  * @return A new copy of this model with the specified created
	  */
	override def withCreated(created: Instant) = copy(created = Some(created))
	
	override def withId(id: Int) = copy(id = Some(id))
	
	/**
	  * @param messageId (Unique) id given to this message by the sender
	  * @return A new copy of this model with the specified message id
	  */
	override def withMessageId(messageId: String) = copy(messageId = messageId)
	
	/**
	  * @param replyToId Id of the message this message replies to, if applicable
	  * @return A new copy of this model with the specified reply to id
	  */
	override def withReplyToId(replyToId: Int) = copy(replyToId = Some(replyToId))
	
	/**
	  * @param senderId Id of the address from which this message was sent
	  * @return A new copy of this model with the specified sender id
	  */
	override def withSenderId(senderId: Int) = copy(senderId = Some(senderId))
	
	/**
	  * @param threadId Id of the thread to which this message belongs
	  * @return A new copy of this model with the specified thread id
	  */
	override def withThreadId(threadId: Int) = copy(threadId = Some(threadId))
}

