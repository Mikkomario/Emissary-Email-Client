package vf.emissary.database.access.many.messaging.message

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import vf.emissary.database.model.messaging.MessageModel

import java.time.Instant

/**
  * A common trait for access points which target multiple messages or similar instances at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait ManyMessagesAccessLike[+A, +Repr] extends ManyModelAccess[A] with Indexed with FilterableView[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * thread ids of the accessible messages
	  */
	def threadIds(implicit connection: Connection) = pullColumn(model.threadIdColumn).map { v => v.getInt }
	
	/**
	  * sender ids of the accessible messages
	  */
	def senderIds(implicit connection: Connection) = pullColumn(model.senderIdColumn).map { v => v.getInt }
	
	/**
	  * message ids of the accessible messages
	  */
	def messageIds(implicit connection: Connection) = pullColumn(model.messageIdColumn).flatMap { _.string }
	
	/**
	  * reply to ids of the accessible messages
	  */
	def replyToIds(implicit connection: Connection) = pullColumn(model.replyToIdColumn).flatMap { v => v.int }
	
	/**
	  * creation times of the accessible messages
	  */
	def creationTimes(implicit connection: Connection) = pullColumn(model.createdColumn)
		.map { v => v.getInstant }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
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
	def creationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the message ids of the targeted messages
	  * @param newMessageId A new message id to assign
	  * @return Whether any message was affected
	  */
	def messageIds_=(newMessageId: String)(implicit connection: Connection) = 
		putColumn(model.messageIdColumn, newMessageId)
	
	/**
	  * Updates the reply to ids of the targeted messages
	  * @param newReplyToId A new reply to id to assign
	  * @return Whether any message was affected
	  */
	def replyToIds_=(newReplyToId: Int)(implicit connection: Connection) = 
		putColumn(model.replyToIdColumn, newReplyToId)
	
	/**
	  * Updates the sender ids of the targeted messages
	  * @param newSenderId A new sender id to assign
	  * @return Whether any message was affected
	  */
	def senderIds_=(newSenderId: Int)(implicit connection: Connection) = 
		putColumn(model.senderIdColumn, newSenderId)
	
	/**
	  * Updates the thread ids of the targeted messages
	  * @param newThreadId A new thread id to assign
	  * @return Whether any message was affected
	  */
	def threadIds_=(newThreadId: Int)(implicit connection: Connection) = 
		putColumn(model.threadIdColumn, newThreadId)
}

