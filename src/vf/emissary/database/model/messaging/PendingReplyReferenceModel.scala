package vf.emissary.database.model.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.emissary.database.factory.messaging.PendingReplyReferenceFactory
import vf.emissary.model.partial.messaging.PendingReplyReferenceData
import vf.emissary.model.stored.messaging.PendingReplyReference

import java.time.Instant

/**
  * Used for constructing PendingReplyReferenceModel instances and for inserting pending reply references
  *  to the database
  * @author Mikko Hilpinen
  * @since 19.10.2023, v0.1
  */
object PendingReplyReferenceModel 
	extends DataInserter[PendingReplyReferenceModel, PendingReplyReference, PendingReplyReferenceData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains pending reply reference message id
	  */
	val messageIdAttName = "messageId"
	
	/**
	  * Name of the property that contains pending reply reference referenced message id
	  */
	val referencedMessageIdAttName = "referencedMessageId"
	
	/**
	  * Name of the property that contains pending reply reference created
	  */
	val createdAttName = "created"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains pending reply reference message id
	  */
	def messageIdColumn = table(messageIdAttName)
	
	/**
	  * Column that contains pending reply reference referenced message id
	  */
	def referencedMessageIdColumn = table(referencedMessageIdAttName)
	
	/**
	  * Column that contains pending reply reference created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = PendingReplyReferenceFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: PendingReplyReferenceData) = 
		apply(None, Some(data.messageId), data.referencedMessageId, Some(data.created))
	
	override protected def complete(id: Value, data: PendingReplyReferenceData) = 
		PendingReplyReference(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param created Time when this pending reply reference was added to the database
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param id A pending reply reference id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param messageId Id of the message from which this reference is made from
	  * @return A model containing only the specified message id
	  */
	def withMessageId(messageId: Int) = apply(messageId = Some(messageId))
	
	/**
	  * @param referencedMessageId Message id of the referenced message
	  * @return A model containing only the specified referenced message id
	  */
	def withReferencedMessageId(referencedMessageId: String) = apply(referencedMessageId = referencedMessageId)
}

/**
  * Used for interacting with PendingReplyReferences in the database
  * @param id pending reply reference database id
  * @author Mikko Hilpinen
  * @since 19.10.2023, v0.1
  */
case class PendingReplyReferenceModel(id: Option[Int] = None, messageId: Option[Int] = None, 
	referencedMessageId: String = "", created: Option[Instant] = None) 
	extends StorableWithFactory[PendingReplyReference]
{
	// IMPLEMENTED	--------------------
	
	override def factory = PendingReplyReferenceModel.factory
	
	override def valueProperties = {
		import PendingReplyReferenceModel._
		Vector("id" -> id, messageIdAttName -> messageId, referencedMessageIdAttName -> referencedMessageId, 
			createdAttName -> created)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param created Time when this pending reply reference was added to the database
	  * @return A new copy of this model with the specified created
	  */
	def withCreated(created: Instant) = copy(created = Some(created))
	
	/**
	  * @param messageId Id of the message from which this reference is made from
	  * @return A new copy of this model with the specified message id
	  */
	def withMessageId(messageId: Int) = copy(messageId = Some(messageId))
	
	/**
	  * @param referencedMessageId Message id of the referenced message
	  * @return A new copy of this model with the specified referenced message id
	  */
	def withReferencedMessageId(referencedMessageId: String) = copy(referencedMessageId = referencedMessageId)
}

