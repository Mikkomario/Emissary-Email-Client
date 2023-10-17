package vf.emissary.database.access.single.messaging.message_thread

import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.{Condition, OrderBy}
import vf.emissary.database.factory.messaging.MessageThreadFactory
import vf.emissary.database.model.messaging.{MessageModel, MessageRecipientLinkModel, MessageThreadModel, MessageThreadSubjectLinkModel}
import vf.emissary.model.stored.messaging.MessageThread

/**
  * Used for accessing individual message threads
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbMessageThread extends SingleRowModelAccess[MessageThread] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = MessageThreadModel
	
	private def subjectLinkModel = MessageThreadSubjectLinkModel
	private def messageModel = MessageModel
	private def recipientLinkModel = MessageRecipientLinkModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageThreadFactory
	
	
	// OTHER	--------------------
	
	/**
	 * Starts a new thread
	 * @param connection Implicit DB connection
	 * @return Id of the new thread
	 */
	def newId()(implicit connection: Connection) = model().insert().getInt
	
	/**
	  * @param id Database id of the targeted message thread
	  * @return An access point to that message thread
	  */
	def apply(id: Int) = DbSingleMessageThread(id)
	
	/**
	 * Finds the latest message thread's id that involves the specified address and uses the specified subject
	 * @param subjectId Id of the required subject
	 * @param addressId Id of the address that must be involved in the returned thread
	 * @param connection Implicit DB connection
	 * @return Id of the most recent thread that fulfills the specified requirements.
	 *         None if no such thread exists.
	 */
	def findIdForPersonalSubject(subjectId: Int, addressId: Int)(implicit connection: Connection) = {
		// Subject must match
		val subjectCondition = subjectLinkModel.withSubjectId(subjectId).toCondition
		// Address must be included in at least a single message as a sender
		val addressCondition = messageModel.withSenderId(addressId).toCondition
		findColumn(index,
			condition = subjectCondition && addressCondition,
			// Selects from the most recent applicable message
			order = Some(OrderBy.descending(messageModel.createdColumn)),
			// Requires 2 joins
			joins = Vector(subjectLinkModel.table, messageModel.table)
		).int
	}
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique message threads.
	  * @return An access point to the message thread that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = UniqueMessageThreadAccess(mergeCondition(condition))
}

