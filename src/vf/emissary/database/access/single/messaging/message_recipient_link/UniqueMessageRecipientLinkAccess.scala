package vf.emissary.database.access.single.messaging.message_recipient_link

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.MessageRecipientLinkFactory
import vf.emissary.database.model.messaging.MessageRecipientLinkModel
import vf.emissary.model.enumeration.RecipientType
import vf.emissary.model.stored.messaging.MessageRecipientLink

object UniqueMessageRecipientLinkAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueMessageRecipientLinkAccess = 
		new _UniqueMessageRecipientLinkAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueMessageRecipientLinkAccess(condition: Condition)
		 extends UniqueMessageRecipientLinkAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct message recipient links.
  * @author Mikko Hilpinen
  * @since 15.10.2023, v0.1
  */
trait UniqueMessageRecipientLinkAccess 
	extends SingleRowModelAccess[MessageRecipientLink] with FilterableView[UniqueMessageRecipientLinkAccess] 
		with DistinctModelAccess[MessageRecipientLink, Option[MessageRecipientLink], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the sent message. None if no message recipient link (or value) was found.
	  */
	def messageId(implicit connection: Connection) = pullColumn(model.messageIdColumn).int
	
	/**
	  * Id of the message recipient (address). None if no message recipient link (or value) was found.
	  */
	def recipientId(implicit connection: Connection) = pullColumn(model.recipientIdColumn).int
	
	/**
	  * Role / type of the message recipient. None if no message recipient link (or value) was found.
	  */
	def role(implicit connection: Connection) = pullColumn(model
		.roleColumn).int.flatMap(RecipientType.findForId)
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = MessageRecipientLinkModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageRecipientLinkFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueMessageRecipientLinkAccess = 
		new UniqueMessageRecipientLinkAccess._UniqueMessageRecipientLinkAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the message ids of the targeted message recipient links
	  * @param newMessageId A new message id to assign
	  * @return Whether any message recipient link was affected
	  */
	def messageId_=(newMessageId: Int)(implicit connection: Connection) = 
		putColumn(model.messageIdColumn, newMessageId)
	
	/**
	  * Updates the recipient ids of the targeted message recipient links
	  * @param newRecipientId A new recipient id to assign
	  * @return Whether any message recipient link was affected
	  */
	def recipientId_=(newRecipientId: Int)(implicit connection: Connection) = 
		putColumn(model.recipientIdColumn, newRecipientId)
	
	/**
	  * Updates the roles of the targeted message recipient links
	  * @param newRole A new role to assign
	  * @return Whether any message recipient link was affected
	  */
	def role_=(newRole: RecipientType)(implicit connection: Connection) = putColumn(model.roleColumn, 
		newRole.id)
}

