package vf.emissary.database.access.many.messaging.message.link.recipient

import utopia.flow.collection.immutable.IntSet
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{FilterableView, ViewFactory}
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.MessageRecipientLinkDbFactory
import vf.emissary.database.storable.messaging.MessageRecipientLinkDbModel
import vf.emissary.model.enumeration.RecipientType
import vf.emissary.model.stored.messaging.MessageRecipientLink

object ManyMessageRecipientLinksAccess extends ViewFactory[ManyMessageRecipientLinksAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyMessageRecipientLinksAccess = 
		_ManyMessageRecipientLinksAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyMessageRecipientLinksAccess(override val accessCondition: Option[Condition]) 
		extends ManyMessageRecipientLinksAccess
}

/**
  * A common trait for access points which target multiple message recipient links at a time
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait ManyMessageRecipientLinksAccess 
	extends ManyRowModelAccess[MessageRecipientLink] with FilterableView[ManyMessageRecipientLinksAccess] 
		with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * message ids of the accessible message recipient links
	  */
	def messageIds(implicit connection: Connection) = pullColumn(model.messageId.column).map { v => v.getInt }
	
	/**
	  * recipient ids of the accessible message recipient links
	  */
	def recipientIds(implicit connection: Connection) = pullColumn(model.recipientId.column).map {
		 v => v.getInt }
	
	/**
	  * roles of the accessible message recipient links
	  */
	def roles(implicit connection: Connection) = 
		pullColumn(model.role.column).map { v => v.getInt }.flatMap(RecipientType.findForId)
	
	/**
	  * Unique ids of the accessible message recipient links
	  */
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = MessageRecipientLinkDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageRecipientLinkDbFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): ManyMessageRecipientLinksAccess = 
		ManyMessageRecipientLinksAccess(condition)
	
	
	// OTHER	--------------------
	
	/**
	  * @param messageId message id to target
	  * @return Copy of this access point that only includes message recipient links 
		with the specified message id
	  */
	def inMessage(messageId: Int) = filter(model.messageId.column <=> messageId)
	
	/**
	  * @param messageIds Targeted message ids
	  * @return
	  * 
		 Copy of this access point that only includes message recipient links where message id is within the specified value set
	  */
	def inMessages(messageIds: IterableOnce[Int]) = filter(model.messageId.column.in(IntSet.from(messageIds)))
	
	/**
	  * @param recipientId recipient id to target
	  * @return Copy of this access point that only includes message recipient links 
		with the specified recipient id
	  */
	def toRecipient(recipientId: Int) = filter(model.recipientId.column <=> recipientId)
	
	/**
	  * @param recipientIds Targeted recipient ids
	  * @return
	  * 
		 Copy of this access point that only includes message recipient links where recipient id is within the specified value set
	  */
	def toRecipients(recipientIds: IterableOnce[Int]) = 
		filter(model.recipientId.column.in(IntSet.from(recipientIds)))
}

