package vf.emissary.database.model.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.emissary.database.factory.messaging.MessageRecipientLinkFactory
import vf.emissary.model.enumeration.RecipientType
import vf.emissary.model.partial.messaging.MessageRecipientLinkData
import vf.emissary.model.stored.messaging.MessageRecipientLink

/**
  * Used for constructing MessageRecipientLinkModel instances and for inserting message recipient links
  *  to the database
  * @author Mikko Hilpinen
  * @since 15.10.2023, v0.1
  */
object MessageRecipientLinkModel 
	extends DataInserter[MessageRecipientLinkModel, MessageRecipientLink, MessageRecipientLinkData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains message recipient link message id
	  */
	val messageIdAttName = "messageId"
	
	/**
	  * Name of the property that contains message recipient link recipient id
	  */
	val recipientIdAttName = "recipientId"
	
	/**
	  * Name of the property that contains message recipient link role
	  */
	val roleAttName = "roleId"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains message recipient link message id
	  */
	def messageIdColumn = table(messageIdAttName)
	
	/**
	  * Column that contains message recipient link recipient id
	  */
	def recipientIdColumn = table(recipientIdAttName)
	
	/**
	  * Column that contains message recipient link role
	  */
	def roleColumn = table(roleAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = MessageRecipientLinkFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: MessageRecipientLinkData) = 
		apply(None, Some(data.messageId), Some(data.recipientId), Some(data.role.id))
	
	override protected def complete(id: Value, data: MessageRecipientLinkData) = 
		MessageRecipientLink(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param id A message recipient link id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param messageId Id of the sent message
	  * @return A model containing only the specified message id
	  */
	def withMessageId(messageId: Int) = apply(messageId = Some(messageId))
	
	/**
	  * @param recipientId Id of the message recipient (address)
	  * @return A model containing only the specified recipient id
	  */
	def withRecipientId(recipientId: Int) = apply(recipientId = Some(recipientId))
	
	/**
	  * @param role Role / type of the message recipient
	  * @return A model containing only the specified role
	  */
	def withRole(role: RecipientType) = apply(role = Some(role.id))
}

/**
  * Used for interacting with MessageRecipientLinks in the database
  * @param id message recipient link database id
  * @author Mikko Hilpinen
  * @since 15.10.2023, v0.1
  */
case class MessageRecipientLinkModel(id: Option[Int] = None, messageId: Option[Int] = None, 
	recipientId: Option[Int] = None, role: Option[Int] = None) 
	extends StorableWithFactory[MessageRecipientLink]
{
	// IMPLEMENTED	--------------------
	
	override def factory = MessageRecipientLinkModel.factory
	
	override def valueProperties = {
		import MessageRecipientLinkModel._
		Vector("id" -> id, messageIdAttName -> messageId, recipientIdAttName -> recipientId, 
			roleAttName -> role)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param messageId Id of the sent message
	  * @return A new copy of this model with the specified message id
	  */
	def withMessageId(messageId: Int) = copy(messageId = Some(messageId))
	
	/**
	  * @param recipientId Id of the message recipient (address)
	  * @return A new copy of this model with the specified recipient id
	  */
	def withRecipientId(recipientId: Int) = copy(recipientId = Some(recipientId))
	
	/**
	  * @param role Role / type of the message recipient
	  * @return A new copy of this model with the specified role
	  */
	def withRole(role: RecipientType) = copy(role = Some(role.id))
}

