package vf.emissary.database.access.many.messaging.message_recipient_link

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.MessageRecipientLinkFactory
import vf.emissary.database.model.messaging.MessageRecipientLinkModel
import vf.emissary.model.enumeration.RecipientType
import vf.emissary.model.stored.messaging.MessageRecipientLink

object ManyMessageRecipientLinksAccess
{
	// NESTED	--------------------
	
	private class ManyMessageRecipientLinksSubView(condition: Condition)
		 extends ManyMessageRecipientLinksAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple message recipient links at a time
  * @author Mikko Hilpinen
  * @since 15.10.2023, v0.1
  */
trait ManyMessageRecipientLinksAccess 
	extends ManyRowModelAccess[MessageRecipientLink] with FilterableView[ManyMessageRecipientLinksAccess] 
		with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * message ids of the accessible message recipient links
	  */
	def messageIds(implicit connection: Connection) = pullColumn(model.messageIdColumn).map { v => v.getInt }
	
	/**
	  * recipient ids of the accessible message recipient links
	  */
	def recipientIds(implicit connection: Connection) = pullColumn(model.recipientIdColumn)
		.map { v => v.getInt }
	
	/**
	  * roles of the accessible message recipient links
	  */
	def roles(implicit connection: Connection) = 
		pullColumn(model.roleColumn).map { v => v.getInt }.flatMap(RecipientType.findForId)
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = MessageRecipientLinkModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageRecipientLinkFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyMessageRecipientLinksAccess = 
		new ManyMessageRecipientLinksAccess.ManyMessageRecipientLinksSubView(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	 * @param messageIds Ids of the targeted messages
	 * @return Access to recipient-links within those messages
	 */
	def inMessages(messageIds: Iterable[Int]) =
		filter(model.messageIdColumn.in(messageIds))
	
	/**
	  * Updates the message ids of the targeted message recipient links
	  * @param newMessageId A new message id to assign
	  * @return Whether any message recipient link was affected
	  */
	def messageIds_=(newMessageId: Int)(implicit connection: Connection) = 
		putColumn(model.messageIdColumn, newMessageId)
	
	/**
	  * Updates the recipient ids of the targeted message recipient links
	  * @param newRecipientId A new recipient id to assign
	  * @return Whether any message recipient link was affected
	  */
	def recipientIds_=(newRecipientId: Int)(implicit connection: Connection) = 
		putColumn(model.recipientIdColumn, newRecipientId)
	
	/**
	  * Updates the roles of the targeted message recipient links
	  * @param newRole A new role to assign
	  * @return Whether any message recipient link was affected
	  */
	def roles_=(newRole: RecipientType)(implicit connection: Connection) = putColumn(model.roleColumn, 
		newRole.id)
}

