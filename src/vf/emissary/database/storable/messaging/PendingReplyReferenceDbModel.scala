package vf.emissary.database.storable.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.HasIdProperty
import utopia.vault.store.{FromIdFactory, HasId}
import utopia.vault.nosql.storable.StorableFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.factory.messaging.PendingReplyReferenceFactory
import vf.emissary.model.partial.messaging.PendingReplyReferenceData
import vf.emissary.model.stored.messaging.PendingReplyReference

import java.time.Instant

/**
  * Used
  * 
	 for constructing PendingReplyReferenceDbModel instances and for inserting pending reply references to the database
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object PendingReplyReferenceDbModel 
	extends StorableFactory[PendingReplyReferenceDbModel, PendingReplyReference, PendingReplyReferenceData] 
		with FromIdFactory[Int, PendingReplyReferenceDbModel] with HasIdProperty 
		with PendingReplyReferenceFactory[PendingReplyReferenceDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with message ids
	  */
	lazy val messageId = property("messageId")
	
	/**
	  * Database property used for interacting with referenced message ids
	  */
	lazy val referencedMessageId = property("referencedMessageId")
	
	/**
	  * Database property used for interacting with creation times
	  */
	lazy val created = property("created")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = EmissaryTables.pendingReplyReference
	
	override def apply(data: PendingReplyReferenceData): PendingReplyReferenceDbModel = 
		apply(None, Some(data.messageId), data.referencedMessageId, Some(data.created))
	
	/**
	  * @param created Time when this pending reply reference was added to the database
	  * @return A model containing only the specified created
	  */
	override def withCreated(created: Instant) = apply(created = Some(created))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	/**
	  * @param messageId Id of the message from which this reference is made from
	  * @return A model containing only the specified message id
	  */
	override def withMessageId(messageId: Int) = apply(messageId = Some(messageId))
	
	/**
	  * @param referencedMessageId Message id of the referenced message
	  * @return A model containing only the specified referenced message id
	  */
	override def withReferencedMessageId(referencedMessageId: String) = 
		apply(referencedMessageId = referencedMessageId)
	
	override protected def complete(id: Value, data: PendingReplyReferenceData) = 
		PendingReplyReference(id.getInt, data)
}

/**
  * Used for interacting with PendingReplyReferences in the database
  * @param id pending reply reference database id
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
case class PendingReplyReferenceDbModel(id: Option[Int] = None, messageId: Option[Int] = None, 
	referencedMessageId: String = "", created: Option[Instant] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, PendingReplyReferenceDbModel] 
		with PendingReplyReferenceFactory[PendingReplyReferenceDbModel]
{
	// IMPLEMENTED	--------------------
	
	override def table = PendingReplyReferenceDbModel.table
	
	override def valueProperties = 
		Vector(PendingReplyReferenceDbModel.id.name -> id, 
			PendingReplyReferenceDbModel.messageId.name -> messageId, 
			PendingReplyReferenceDbModel.referencedMessageId.name -> referencedMessageId, 
			PendingReplyReferenceDbModel.created.name -> created)
	
	/**
	  * @param created Time when this pending reply reference was added to the database
	  * @return A new copy of this model with the specified created
	  */
	override def withCreated(created: Instant) = copy(created = Some(created))
	
	override def withId(id: Int) = copy(id = Some(id))
	
	/**
	  * @param messageId Id of the message from which this reference is made from
	  * @return A new copy of this model with the specified message id
	  */
	override def withMessageId(messageId: Int) = copy(messageId = Some(messageId))
	
	/**
	  * @param referencedMessageId Message id of the referenced message
	  * @return A new copy of this model with the specified referenced message id
	  */
	override def withReferencedMessageId(referencedMessageId: String) = 
		copy(referencedMessageId = referencedMessageId)
}

