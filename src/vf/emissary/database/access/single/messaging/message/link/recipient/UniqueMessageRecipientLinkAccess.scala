package vf.emissary.database.access.single.messaging.message.link.recipient

import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{FilterableView, ViewFactory}
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.MessageRecipientLinkDbFactory
import vf.emissary.database.storable.messaging.MessageRecipientLinkDbModel
import vf.emissary.model.enumeration.RecipientType
import vf.emissary.model.stored.messaging.MessageRecipientLink

object UniqueMessageRecipientLinkAccess extends ViewFactory[UniqueMessageRecipientLinkAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): UniqueMessageRecipientLinkAccess = 
		_UniqueMessageRecipientLinkAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _UniqueMessageRecipientLinkAccess(override val accessCondition: Option[Condition]) 
		extends UniqueMessageRecipientLinkAccess
}

/**
  * A common trait for access points that return individual and distinct message recipient links.
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait UniqueMessageRecipientLinkAccess 
	extends SingleRowModelAccess[MessageRecipientLink] 
		with DistinctModelAccess[MessageRecipientLink, Option[MessageRecipientLink], Value] 
		with FilterableView[UniqueMessageRecipientLinkAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the sent message. 
	  * None if no message recipient link (or value) was found.
	  */
	def messageId(implicit connection: Connection) = pullColumn(model.messageId.column).int
	
	/**
	  * Id of the message recipient (address). 
	  * None if no message recipient link (or value) was found.
	  */
	def recipientId(implicit connection: Connection) = pullColumn(model.recipientId.column).int
	
	/**
	  * Role / type of the message recipient. 
	  * None if no message recipient link (or value) was found.
	  */
	def role(implicit connection: Connection) = pullColumn(model
		.role.column).int.flatMap(RecipientType.findForId)
	
	/**
	  * Unique id of the accessible message recipient link. None if no message recipient link was accessible.
	  */
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = MessageRecipientLinkDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageRecipientLinkDbFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): UniqueMessageRecipientLinkAccess = 
		UniqueMessageRecipientLinkAccess(condition)
}

