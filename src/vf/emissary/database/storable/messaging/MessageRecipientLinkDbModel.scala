package vf.emissary.database.storable.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.HasIdProperty
import utopia.vault.store.{FromIdFactory, HasId}
import utopia.vault.nosql.storable.StorableFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.enumeration.RecipientType
import vf.emissary.model.factory.messaging.MessageRecipientLinkFactory
import vf.emissary.model.partial.messaging.MessageRecipientLinkData
import vf.emissary.model.stored.messaging.MessageRecipientLink

/**
  * Used
  * 
	 for constructing MessageRecipientLinkDbModel instances and for inserting message recipient links to the database
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object MessageRecipientLinkDbModel 
	extends StorableFactory[MessageRecipientLinkDbModel, MessageRecipientLink, MessageRecipientLinkData] 
		with FromIdFactory[Int, MessageRecipientLinkDbModel] with HasIdProperty 
		with MessageRecipientLinkFactory[MessageRecipientLinkDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with message ids
	  */
	lazy val messageId = property("messageId")
	
	/**
	  * Database property used for interacting with recipient ids
	  */
	lazy val recipientId = property("recipientId")
	
	/**
	  * Database property used for interacting with roles
	  */
	lazy val role = property("roleId")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = EmissaryTables.messageRecipientLink
	
	override def apply(data: MessageRecipientLinkData): MessageRecipientLinkDbModel = 
		apply(None, Some(data.messageId), Some(data.recipientId), Some(data.role))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	/**
	  * @param messageId Id of the sent message
	  * @return A model containing only the specified message id
	  */
	override def withMessageId(messageId: Int) = apply(messageId = Some(messageId))
	
	/**
	  * @param recipientId Id of the message recipient (address)
	  * @return A model containing only the specified recipient id
	  */
	override def withRecipientId(recipientId: Int) = apply(recipientId = Some(recipientId))
	
	/**
	  * @param role Role / type of the message recipient
	  * @return A model containing only the specified role
	  */
	override def withRole(role: RecipientType) = apply(role = Some(role))
	
	override protected def complete(id: Value, data: MessageRecipientLinkData) = 
		MessageRecipientLink(id.getInt, data)
}

/**
  * Used for interacting with MessageRecipientLinks in the database
  * @param id message recipient link database id
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
case class MessageRecipientLinkDbModel(id: Option[Int] = None, messageId: Option[Int] = None, 
	recipientId: Option[Int] = None, role: Option[RecipientType] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, MessageRecipientLinkDbModel] 
		with MessageRecipientLinkFactory[MessageRecipientLinkDbModel]
{
	// IMPLEMENTED	--------------------
	
	override def table = MessageRecipientLinkDbModel.table
	
	override def valueProperties = 
		Vector(MessageRecipientLinkDbModel.id.name -> id, 
			MessageRecipientLinkDbModel.messageId.name -> messageId, 
			MessageRecipientLinkDbModel.recipientId.name -> recipientId, 
			MessageRecipientLinkDbModel.role.name -> role.map[Value] { e => e.id }.getOrElse(Value.empty))
	
	override def withId(id: Int) = copy(id = Some(id))
	
	/**
	  * @param messageId Id of the sent message
	  * @return A new copy of this model with the specified message id
	  */
	override def withMessageId(messageId: Int) = copy(messageId = Some(messageId))
	
	/**
	  * @param recipientId Id of the message recipient (address)
	  * @return A new copy of this model with the specified recipient id
	  */
	override def withRecipientId(recipientId: Int) = copy(recipientId = Some(recipientId))
	
	/**
	  * @param role Role / type of the message recipient
	  * @return A new copy of this model with the specified role
	  */
	override def withRole(role: RecipientType) = copy(role = Some(role))
}

