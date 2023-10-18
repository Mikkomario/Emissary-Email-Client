package vf.emissary.database.access.many.messaging.message

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.model.template.Joinable
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.ChronoRowFactoryView
import utopia.vault.sql.{Condition, JoinType}
import vf.emissary.database.factory.messaging.MessageFactory
import vf.emissary.database.model.messaging.{MessageRecipientLinkModel, MessageStatementLinkModel}
import vf.emissary.model.stored.messaging.Message

object ManyMessagesAccess
{
	// NESTED	--------------------
	
	private class ManyMessagesSubView(condition: Condition) extends ManyMessagesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple messages at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait ManyMessagesAccess 
	extends ManyMessagesAccessLike[Message, ManyMessagesAccess] with ManyRowModelAccess[Message] 
		with ChronoRowFactoryView[Message, ManyMessagesAccess]
{
	// COMPUTED ------------------------
	
	/**
	 * @return Model used for interacting with message-statement-links
	 */
	protected def statementLinkModel = MessageStatementLinkModel
	/**
	 * @return Model used for interacting with message recipients
	 */
	protected def recipientModel = MessageRecipientLinkModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyMessagesAccess = 
		new ManyMessagesAccess.ManyMessagesSubView(mergeCondition(filterCondition))
		
	
	// OTHER    ------------------------
	
	/**
	 * Finds all accessible messages that involve the specified address as either a recipient or a sender
	 * @param addressId Id of the targeted address
	 * @param connection Implicit DB Connection
	 * @return Accessible messages involving the specified address
	 */
	def findInvolvingAddress(addressId: Int)(implicit connection: Connection) =
		find(model.withSenderId(addressId).toCondition || recipientModel.withRecipientId(addressId).toCondition,
			joins = Vector(recipientModel.table), joinType = JoinType.Left)
	/**
	 * Finds all accessible messages that involve any of the specified addresses as either a recipient or a sender
	 * @param addressIds  Id of the targeted addresses
	 * @param connection Implicit DB Connection
	 * @return Accessible messages involving specified addresses
	 */
	def findInvolvingAddresses(addressIds: Iterable[Int])(implicit connection: Connection) =
		find(involvesCondition(addressIds), joins = Vector(recipientModel.table), joinType = JoinType.Left)
	/**
	 * @param addressIds Ids of the targeted addresses
	 * @param connection Implicit DB connection
	 * @return Ids of the message threads that involve the specified addresses in either sender or recipient role
	 */
	def findThreadIdsInvolvingAddresses(addressIds: Iterable[Int])(implicit connection: Connection) =
		findColumn(model.threadIdColumn, involvesCondition(addressIds),
			joins = Vector(recipientModel.table), joinType = JoinType.Left)
			.view.map { _.getInt }.toSet
	
	/**
	 * @param statementIds Ids of the targeted statements
	 * @param connection Implicit DB connection
	 * @return Accessible messages that make any of the specified statements
	 */
	def findMakingStatements(statementIds: Iterable[Int])(implicit connection: Connection) =
		find(statementCondition(statementIds), joins = Vector(statementLinkModel.table))
	/**
	 * @param statementIds Ids of the targeted statements
	 * @param connection   Implicit DB connection
	 * @return Accessible message thread ids that make any of the specified statements within their messages
	 */
	def findThreadIdsMakingStatements(statementIds: Iterable[Int])(implicit connection: Connection) =
		findColumn(model.threadIdColumn, statementCondition(statementIds), joins = Vector(statementLinkModel.table))
			.view.map { _.getInt }.toSet
	
	/**
	 * Finds all accessible messages that involve at least one of the specified addresses and make at least one
	 * of the specified statements
	 * @param statementIds Ids of the targeted statements
	 * @param addressIds Ids of the targeted addresses
	 * @param connection Implicit DB connection
	 * @return Accessible messages linked to those statements and addresses
	 */
	def findMakingStatementsAndInvolvingAddresses(statementIds: Iterable[Int], addressIds: Iterable[Int])
	                                             (implicit connection: Connection) =
		_findMakingStatementsAndInvolvingAddresses(statementIds, addressIds) { (c, j, jt) =>
			find(c, joins = j, joinType = jt)
		}
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
			findColumn(model.threadIdColumn, c, joins = j, joinType = jt)
		}.view.map { _.getInt }.toSet
	
	private def _findMakingStatementsAndInvolvingAddresses[A](statementIds: Iterable[Int], addressIds: Iterable[Int])
	                                                         (find: (Condition, Vector[Joinable], JoinType) => Vector[A]) =
		find(statementCondition(statementIds) && involvesCondition(addressIds),
			Vector[Joinable](statementLinkModel.table, recipientModel.table), JoinType.Left)
	
	private def involvesCondition(addressIds: Iterable[Int]) =
		model.senderIdColumn.in(addressIds) || recipientModel.recipientIdColumn.in(addressIds)
	
	private def statementCondition(statementIds: Iterable[Int]) =
		statementLinkModel.statementIdColumn.in(statementIds)
}

