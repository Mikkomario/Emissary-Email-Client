package vf.emissary.database.access.many.messaging.message

import utopia.flow.collection.immutable.{IntSet, Pair, Single}
import utopia.flow.collection.CollectionExtensions._
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.model.template.Joinable
import utopia.vault.nosql.access.many.model.ManyModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.{Condition, JoinType}
import vf.emissary.database.storable.messaging.{MessageDbModel, MessageRecipientLinkDbModel, MessageStatementLinkDbModel}

/**
  * A common trait for access points which target multiple messages or similar instances at a time
  * @tparam A Type of read (messages -like) instances
  * @tparam Repr Type of this access point
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait ManyMessagesAccessLike[+A, +Repr] extends ManyModelAccess[A] with Indexed with FilterableView[Repr]
{
	// COMPUTED	--------------------
	
	/**
	 * Access to those messages that have a message id
	 */
	def withMessageId = filter(model.messageId.isNotNull)
	
	/**
	  * thread ids of the accessible messages
	  */
	def threadIds(implicit connection: Connection) = pullColumn(model.threadId.column).map { v => v.getInt }
	/**
	  * sender ids of the accessible messages
	  */
	def senderIds(implicit connection: Connection) = pullColumn(model.senderId.column).map { v => v.getInt }
	/**
	  * message ids of the accessible messages
	  */
	def messageIds(implicit connection: Connection) = pullColumn(model.messageId.column).flatMap { _.string }
	/**
	  * reply to ids of the accessible messages
	  */
	def replyToIds(implicit connection: Connection) = pullColumn(model.replyToId.column).flatMap { v => v.int }
	/**
	  * creation times of the accessible messages
	  */
	def creationTimes(implicit connection: Connection) = 
		pullColumn(model.created.column).map { v => v.getInstant }
	/**
	  * Unique ids of the accessible messages
	  */
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Accessible messages as a map where keys are message ids (strings)
	  * and values are matching database message row ids.
	  * @param connection Implicit DB connection
	  */
	def messageIdMap(implicit connection: Connection) =
		pullColumnMap(model.messageId, index).map { case (mIdVal, idVal) => mIdVal.getString -> idVal.getInt }
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = MessageDbModel
	
	/**
	 * Model used for interacting with message recipients
	 */
	protected def recipientModel = MessageRecipientLinkDbModel
	/**
	 * Model used for interacting with message-statement-links
	 */
	protected def statementLinkModel = MessageStatementLinkDbModel
	
	
	// OTHER	--------------------
	
	/**
	 * @param messageId message id to target
	 * @return Copy of this access point that only includes messages with the specified message id
	 */
	def withMessageId(messageId: String) = filter(model.messageId.column <=> messageId)
	/**
	 * @param ids Targeted message ids
	 * @return Access to messages with those ids
	 */
	def withMessageIds(ids: Iterable[String]) = filter(model.messageId.column.in(ids))
	
	/**
	  * @param threadId thread id to target
	  * @return Copy of this access point that only includes messages with the specified thread id
	  */
	def inThread(threadId: Int) = filter(model.threadId.column <=> threadId)
	/**
	  * @param threadIds Targeted thread ids
	  * @return Copy of this access point that only includes messages where thread id is within the specified value set
	  */
	def inThreads(threadIds: IterableOnce[Int]) = filter(model.threadId.column.in(IntSet.from(threadIds)))
	
	/**
	  * @param threadIds Ids of the threads to exclude
	  * @return Access to messages outside the specified threads
	  */
	// Won't perform filtering if the specified set is empty
	def outsideThreads(threadIds: Iterable[Int]) = 
		if (threadIds.isEmpty) self else filter(model.threadId.notIn(threadIds))
	
	/**
	 * @param senderId sender id to target
	 * @return Copy of this access point that only includes messages with the specified sender id
	 */
	def sentBy(senderId: Int) = filter(model.senderId.column <=> senderId)
	/**
	 * @param senderIds Targeted sender ids
	 * @return Copy of this access point that only includes messages where sender id is within the specified value set
	 */
	def sentByAddresses(senderIds: IterableOnce[Int]) = filter(model.senderId.column.in(IntSet.from(senderIds)))
	
	/**
	 * @param replyToId reply to id to target
	 * @return Copy of this access point that only includes messages with the specified reply to id
	 */
	def replyingTo(replyToId: Int) = filter(model.replyToId.column <=> replyToId)
	/**
	 * @param replyToIds Targeted reply to ids
	 * @return Copy of this access point that only includes messages where reply to id is within the specified value set
	 */
	def replyingToMessages(replyToIds: IterableOnce[Int]) =
		filter(model.replyToId.column.in(IntSet.from(replyToIds)))
	
	/**
	 * Finds all accessible messages that involve the specified address as either a recipient or a sender
	 * @param addressId Id of the targeted address
	 * @param connection Implicit DB Connection
	 * @return Accessible messages involving the specified address
	 */
	def findInvolvingAddress(addressId: Int)(implicit connection: Connection) =
		find(model.senderId <=> addressId || recipientModel.recipientId <=> addressId,
			joins = Single(recipientModel.table), joinType = JoinType.Left)
	/**
	 * Finds all accessible messages that involve any of the specified addresses as either a recipient or a sender
	 * @param addressIds  Id of the targeted addresses
	 * @param connection Implicit DB Connection
	 * @return Accessible messages involving specified addresses
	 */
	def findInvolvingAddresses(addressIds: Iterable[Int])(implicit connection: Connection) =
		find(involvesCondition(addressIds), joins = Vector(recipientModel.table), joinType = JoinType.Left)
	
	/**
	 * @param statementIds Ids of the targeted statements
	 * @param connection Implicit DB connection
	 * @return Accessible messages that make any of the specified statements
	 */
	def findMakingStatements(statementIds: Iterable[Int])(implicit connection: Connection) =
		find(statementCondition(statementIds), joins = Vector(statementLinkModel.table))
	
	/**
	 * Finds all accessible messages that involve at least one of the specified addresses and make at least one
	 * of the specified statements
	 * @param statementIds Ids of the targeted statements
	 * @param addressIds Ids of the targeted addresses
	 * @param connection Implicit DB connection
	 * @return Accessible messages linked to those statements and addresses
	 */
	def findMakingStatementsAndInvolvingAddresses(statementIds: Iterable[Int],
	                                              addressIds: Iterable[Int])(implicit connection: Connection) =
		_findMakingStatementsAndInvolvingAddresses(statementIds, addressIds) { (c, j, jt) =>
			find(c, joins = j, joinType = jt)
		}
	
	/**
	 * @param addressIds Ids of the targeted addresses
	 * @param connection Implicit DB connection
	 * @return Ids of the message threads that involve the specified addresses in either sender or recipient role
	 */
	def findThreadIdsInvolvingAddresses(addressIds: Iterable[Int])(implicit connection: Connection) =
		findColumn(model.threadId, involvesCondition(addressIds),joins = Vector(recipientModel.table),
			joinType = JoinType.Left)
			.view.map { _.getInt }.toSet
	/**
	 * @param statementIds Ids of the targeted statements
	 * @param connection   Implicit DB connection
	 * @return Accessible message thread ids that make any of the specified statements within their messages
	 */
	def findThreadIdsMakingStatements(statementIds: Iterable[Int])(implicit connection: Connection) =
		findColumn(model.threadId, statementCondition(statementIds),
			joins = Single(statementLinkModel.table))
			.view.map { _.getInt }.toSet
	/**
	 * Finds all accessible message thread ids where the messages involve at least one of the specified addresses
	 * and make at least one of the specified statements
	 * @param statementIds Ids of the targeted statements
	 * @param addressIds   Ids of the targeted addresses
	 * @param connection   Implicit DB connection
	 * @return Accessible message-related thread ids linked to those statements and addresses
	 */
	def findThreadIdsMakingStatementsAndInvolvingAddresses(statementIds: Iterable[Int], addressIds: Iterable[Int])
	                                                      (implicit connection: Connection) =
		_findMakingStatementsAndInvolvingAddresses(statementIds, addressIds) { (c, j, jt) =>
			findColumn(model.threadId, c, joins = j, joinType = jt)
		}.view.map { _.getInt }.toSet
	
	/**
	  * Updates the reply to ids of the targeted messages
	  * @param newReplyToId A new reply to id to assign
	  * @return Whether any message was affected
	  */
	def replyToIds_=(newReplyToId: Int)(implicit connection: Connection) = 
		putColumn(model.replyToId.column, newReplyToId)
	
	/**
	 * @param addressIds Ids of the targeted address
	 * @return A condition that tests for the specified addresses as senders or recipients.
	 *         Note: Requires joining to the recipient table in order to work.
	 */
	protected def involvesCondition(addressIds: IterableOnce[Int]) = {
		val idSet = addressIds.toIntSet
		model.senderId.in(idSet) || recipientModel.recipientId.in(idSet)
	}
	/**
	 * @param statementIds Ids of the targeted statements
	 * @return A condition for searching for messages that make the specified statements.
	 *         Note: Requires joining to the statement link table in order to work.
	 */
	protected def statementCondition(statementIds: Iterable[Int]) = statementLinkModel.statementId.in(statementIds)
	
	private def _findMakingStatementsAndInvolvingAddresses[B](statementIds: Iterable[Int], addressIds: Iterable[Int])
	                                                         (find: (Condition, Seq[Joinable], JoinType) => Seq[B]) =
		find(statementCondition(statementIds) && involvesCondition(addressIds),
			Pair[Joinable](statementLinkModel.table, recipientModel.table), JoinType.Left)
}

