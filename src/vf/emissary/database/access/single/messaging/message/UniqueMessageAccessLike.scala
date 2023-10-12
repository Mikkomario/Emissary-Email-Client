package vf.emissary.database.access.single.messaging.message

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import vf.emissary.database.model.messaging.MessageModel

import java.time.Instant

/**
  * A common trait for access points which target individual messages or similar items at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait UniqueMessageAccessLike[+A] 
	extends SingleModelAccess[A] with DistinctModelAccess[A, Option[A], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the thread to which this message belongs. None if no message (or value) was found.
	  */
	def threadId(implicit connection: Connection) = pullColumn(model.threadIdColumn).int
	
	/**
	  * Id of the address from which this message was sent. None if no message (or value) was found.
	  */
	def senderId(implicit connection: Connection) = pullColumn(model.senderIdColumn).int
	
	/**
	  * (Unique) id given to this message by the sender. None if no message (or value) was found.
	  */
	def messageId(implicit connection: Connection) = pullColumn(model.messageIdColumn).getString
	
	/**
	  * Id of the message this message replies to, if applicable. None if no message (or value) was found.
	  */
	def replyToId(implicit connection: Connection) = pullColumn(model.replyToIdColumn).int
	
	/**
	  * Time when this message was sent. None if no message (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.createdColumn).instant
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = MessageModel
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the creation times of the targeted messages
	  * @param newCreated A new created to assign
	  * @return Whether any message was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the message ids of the targeted messages
	  * @param newMessageId A new message id to assign
	  * @return Whether any message was affected
	  */
	def messageId_=(newMessageId: String)(implicit connection: Connection) = 
		putColumn(model.messageIdColumn, newMessageId)
	
	/**
	  * Updates the reply to ids of the targeted messages
	  * @param newReplyToId A new reply to id to assign
	  * @return Whether any message was affected
	  */
	def replyToId_=(newReplyToId: Int)(implicit connection: Connection) = 
		putColumn(model.replyToIdColumn, newReplyToId)
	
	/**
	  * Updates the sender ids of the targeted messages
	  * @param newSenderId A new sender id to assign
	  * @return Whether any message was affected
	  */
	def senderId_=(newSenderId: Int)(implicit connection: Connection) = 
		putColumn(model.senderIdColumn, newSenderId)
	
	/**
	  * Updates the thread ids of the targeted messages
	  * @param newThreadId A new thread id to assign
	  * @return Whether any message was affected
	  */
	def threadId_=(newThreadId: Int)(implicit connection: Connection) = 
		putColumn(model.threadIdColumn, newThreadId)
}

