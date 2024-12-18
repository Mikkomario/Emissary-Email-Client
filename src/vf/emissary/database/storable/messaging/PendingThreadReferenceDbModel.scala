package vf.emissary.database.storable.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{FromIdFactory, HasId, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.factory.messaging.PendingThreadReferenceFactory
import vf.emissary.model.partial.messaging.PendingThreadReferenceData
import vf.emissary.model.stored.messaging.PendingThreadReference

import java.time.Instant

/**
  * Used
  * 
	 for constructing PendingThreadReferenceDbModel instances and for inserting pending thread references to the database
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object PendingThreadReferenceDbModel 
	extends StorableFactory[PendingThreadReferenceDbModel, PendingThreadReference, PendingThreadReferenceData] 
		with FromIdFactory[Int, PendingThreadReferenceDbModel] with HasIdProperty 
		with PendingThreadReferenceFactory[PendingThreadReferenceDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with thread ids
	  */
	lazy val threadId = property("threadId")
	
	/**
	  * Database property used for interacting with referenced message ids
	  */
	lazy val referencedMessageId = property("referencedMessageId")
	
	/**
	  * Database property used for interacting with creation times
	  */
	lazy val created = property("created")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = EmissaryTables.pendingThreadReference
	
	override def apply(data: PendingThreadReferenceData): PendingThreadReferenceDbModel = 
		apply(None, Some(data.threadId), data.referencedMessageId, Some(data.created))
	
	/**
	  * @param created Time when this pending thread reference was added to the database
	  * @return A model containing only the specified created
	  */
	override def withCreated(created: Instant) = apply(created = Some(created))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	/**
	  * @param referencedMessageId Message id belonging to some unread message in the linked thread
	  * @return A model containing only the specified referenced message id
	  */
	override def withReferencedMessageId(referencedMessageId: String) = 
		apply(referencedMessageId = referencedMessageId)
	
	/**
	  * @param threadId Id of the message thread with which the referenced message is linked to
	  * @return A model containing only the specified thread id
	  */
	override def withThreadId(threadId: Int) = apply(threadId = Some(threadId))
	
	override protected def complete(id: Value, data: PendingThreadReferenceData) = 
		PendingThreadReference(id.getInt, data)
}

/**
  * Used for interacting with PendingThreadReferences in the database
  * @param id pending thread reference database id
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
case class PendingThreadReferenceDbModel(id: Option[Int] = None, threadId: Option[Int] = None, 
	referencedMessageId: String = "", created: Option[Instant] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, PendingThreadReferenceDbModel] 
		with PendingThreadReferenceFactory[PendingThreadReferenceDbModel]
{
	// IMPLEMENTED	--------------------
	
	override def table = PendingThreadReferenceDbModel.table
	
	override def valueProperties = 
		Vector(PendingThreadReferenceDbModel.id.name -> id, 
			PendingThreadReferenceDbModel.threadId.name -> threadId, 
			PendingThreadReferenceDbModel.referencedMessageId.name -> referencedMessageId, 
			PendingThreadReferenceDbModel.created.name -> created)
	
	/**
	  * @param created Time when this pending thread reference was added to the database
	  * @return A new copy of this model with the specified created
	  */
	override def withCreated(created: Instant) = copy(created = Some(created))
	
	override def withId(id: Int) = copy(id = Some(id))
	
	/**
	  * @param referencedMessageId Message id belonging to some unread message in the linked thread
	  * @return A new copy of this model with the specified referenced message id
	  */
	override def withReferencedMessageId(referencedMessageId: String) = 
		copy(referencedMessageId = referencedMessageId)
	
	/**
	  * @param threadId Id of the message thread with which the referenced message is linked to
	  * @return A new copy of this model with the specified thread id
	  */
	override def withThreadId(threadId: Int) = copy(threadId = Some(threadId))
}

