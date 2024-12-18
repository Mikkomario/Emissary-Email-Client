package vf.emissary.database.access.single.messaging.message

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import vf.emissary.database.storable.messaging.MessageDbModel

/**
  * A common trait for access points which target individual messages or similar items at a time
  * @tparam A Type of read (messages -like) instances
  * @tparam Repr Type of this access point
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait UniqueMessageAccessLike[+A, +Repr] 
	extends SingleModelAccess[A] with DistinctModelAccess[A, Option[A], Value] with FilterableView[Repr] 
		with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the thread to which this message belongs. 
	  * None if no message (or value) was found.
	  */
	def threadId(implicit connection: Connection) = pullColumn(model.threadId.column).int
	/**
	  * Id of the address from which this message was sent. 
	  * None if no message (or value) was found.
	  */
	def senderId(implicit connection: Connection) = pullColumn(model.senderId.column).int
	/**
	  * (Unique) id given to this message by the sender. 
	  * None if no message (or value) was found.
	  */
	def messageId(implicit connection: Connection) = pullColumn(model.messageId.column).getString
	/**
	  * Id of the message this message replies to, if applicable. 
	  * None if no message (or value) was found.
	  */
	def replyToId(implicit connection: Connection) = pullColumn(model.replyToId.column).int
	/**
	  * Time when this message was sent. 
	  * None if no message (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.created.column).instant
	/**
	  * Unique id of the accessible message. None if no message was accessible.
	  */
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = MessageDbModel
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the reply to ids of the targeted messages
	  * @param newReplyToId A new reply to id to assign
	  * @return Whether any message was affected
	  */
	def replyToId_=(newReplyToId: Int)(implicit connection: Connection) = 
		putColumn(model.replyToId.column, newReplyToId)
}

