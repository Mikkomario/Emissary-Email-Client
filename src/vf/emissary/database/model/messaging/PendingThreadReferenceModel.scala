package vf.emissary.database.model.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.emissary.database.factory.messaging.PendingThreadReferenceFactory
import vf.emissary.model.partial.messaging.PendingThreadReferenceData
import vf.emissary.model.stored.messaging.PendingThreadReference

import java.time.Instant

/**
  * Used for constructing PendingThreadReferenceModel instances and for inserting pending thread references
  *  to the database
  * @author Mikko Hilpinen
  * @since 19.10.2023, v0.1
  */
object PendingThreadReferenceModel 
	extends DataInserter[PendingThreadReferenceModel, PendingThreadReference, PendingThreadReferenceData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains pending thread reference thread id
	  */
	val threadIdAttName = "threadId"
	
	/**
	  * Name of the property that contains pending thread reference referenced message id
	  */
	val referencedMessageIdAttName = "referencedMessageId"
	
	/**
	  * Name of the property that contains pending thread reference created
	  */
	val createdAttName = "created"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains pending thread reference thread id
	  */
	def threadIdColumn = table(threadIdAttName)
	
	/**
	  * Column that contains pending thread reference referenced message id
	  */
	def referencedMessageIdColumn = table(referencedMessageIdAttName)
	
	/**
	  * Column that contains pending thread reference created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = PendingThreadReferenceFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: PendingThreadReferenceData) = 
		apply(None, Some(data.threadId), data.referencedMessageId, Some(data.created))
	
	override protected def complete(id: Value, data: PendingThreadReferenceData) = 
		PendingThreadReference(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param created Time when this pending thread reference was added to the database
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param id A pending thread reference id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param referencedMessageId Message id belonging to some unread message in the linked thread
	  * @return A model containing only the specified referenced message id
	  */
	def withReferencedMessageId(referencedMessageId: String) = apply(referencedMessageId = referencedMessageId)
	
	/**
	  * @param threadId Id of the message thread with which the referenced message is linked to
	  * @return A model containing only the specified thread id
	  */
	def withThreadId(threadId: Int) = apply(threadId = Some(threadId))
}

/**
  * Used for interacting with PendingThreadReferences in the database
  * @param id pending thread reference database id
  * @author Mikko Hilpinen
  * @since 19.10.2023, v0.1
  */
case class PendingThreadReferenceModel(id: Option[Int] = None, threadId: Option[Int] = None, 
	referencedMessageId: String = "", created: Option[Instant] = None) 
	extends StorableWithFactory[PendingThreadReference]
{
	// IMPLEMENTED	--------------------
	
	override def factory = PendingThreadReferenceModel.factory
	
	override def valueProperties = {
		import PendingThreadReferenceModel._
		Vector("id" -> id, threadIdAttName -> threadId, referencedMessageIdAttName -> referencedMessageId, 
			createdAttName -> created)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param created Time when this pending thread reference was added to the database
	  * @return A new copy of this model with the specified created
	  */
	def withCreated(created: Instant) = copy(created = Some(created))
	
	/**
	  * @param referencedMessageId Message id belonging to some unread message in the linked thread
	  * @return A new copy of this model with the specified referenced message id
	  */
	def withReferencedMessageId(referencedMessageId: String) = copy(referencedMessageId = referencedMessageId)
	
	/**
	  * @param threadId Id of the message thread with which the referenced message is linked to
	  * @return A new copy of this model with the specified thread id
	  */
	def withThreadId(threadId: Int) = copy(threadId = Some(threadId))
}

