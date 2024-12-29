package vf.emissary.database.storable.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{FromIdFactory, HasId, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.factory.messaging.AttachmentMessageLinkFactory
import vf.emissary.model.partial.messaging.AttachmentMessageLinkData
import vf.emissary.model.stored.messaging.AttachmentMessageLink

/**
  * Used for constructing AttachmentMessageLinkDbModel instances and for inserting attachment 
  * message links to the database
  * @author Mikko Hilpinen
  * @since 27.12.2024, v1.1
  */
object AttachmentMessageLinkDbModel 
	extends StorableFactory[AttachmentMessageLinkDbModel, AttachmentMessageLink, AttachmentMessageLinkData] 
		with FromIdFactory[Int, AttachmentMessageLinkDbModel] with HasIdProperty 
		with AttachmentMessageLinkFactory[AttachmentMessageLinkDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with attachment ids
	  */
	lazy val attachmentId = property("attachmentId")
	
	/**
	  * Database property used for interacting with message ids
	  */
	lazy val messageId = property("messageId")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = EmissaryTables.attachmentMessageLink
	
	override def apply(data: AttachmentMessageLinkData): AttachmentMessageLinkDbModel = 
		apply(None, Some(data.attachmentId), Some(data.messageId))
	
	/**
	  * @param attachmentId Id of the linked attachment
	  * @return A model containing only the specified attachment id
	  */
	override def withAttachmentId(attachmentId: Int) = apply(attachmentId = Some(attachmentId))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	/**
	  * @param messageId Id of the message in which the attachment appears
	  * @return A model containing only the specified message id
	  */
	override def withMessageId(messageId: Int) = apply(messageId = Some(messageId))
	
	override protected def complete(id: Value, data: AttachmentMessageLinkData) = 
		AttachmentMessageLink(id.getInt, data)
}

/**
  * Used for interacting with AttachmentMessageLinks in the database
  * @param id attachment message link database id
  * @author Mikko Hilpinen
  * @since 27.12.2024, v1.1
  */
case class AttachmentMessageLinkDbModel(id: Option[Int] = None, attachmentId: Option[Int] = None, 
	messageId: Option[Int] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, AttachmentMessageLinkDbModel] 
		with AttachmentMessageLinkFactory[AttachmentMessageLinkDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties = 
		Vector(AttachmentMessageLinkDbModel.id.name -> id, 
			AttachmentMessageLinkDbModel.attachmentId.name -> attachmentId, 
			AttachmentMessageLinkDbModel.messageId.name -> messageId)
	
	
	// IMPLEMENTED	--------------------
	
	override def table = AttachmentMessageLinkDbModel.table
	
	/**
	  * @param attachmentId Id of the linked attachment
	  * @return A new copy of this model with the specified attachment id
	  */
	override def withAttachmentId(attachmentId: Int) = copy(attachmentId = Some(attachmentId))
	
	override def withId(id: Int) = copy(id = Some(id))
	
	/**
	  * @param messageId Id of the message in which the attachment appears
	  * @return A new copy of this model with the specified message id
	  */
	override def withMessageId(messageId: Int) = copy(messageId = Some(messageId))
}

