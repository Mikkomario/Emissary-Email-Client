package vf.emissary.model.stored.messaging

import utopia.flow.generic.model.template.ModelLike.AnyModel
import utopia.vault.store.{FromIdFactory, StoredFromModelFactory, StoredModelConvertible}
import vf.emissary.database.access.single.messaging.reference.pending.reply.DbSinglePendingReplyReference
import vf.emissary.model.factory.messaging.PendingReplyReferenceFactoryWrapper
import vf.emissary.model.partial.messaging.PendingReplyReferenceData

object PendingReplyReference extends StoredFromModelFactory[PendingReplyReferenceData, PendingReplyReference]
{
	// IMPLEMENTED	--------------------
	
	override def dataFactory = PendingReplyReferenceData
	
	override protected def complete(model: AnyModel, data: PendingReplyReferenceData) = 
		model("id").tryInt.map { apply(_, data) }
}

/**
  * Represents a pending reply reference that has already been stored in the database
  * @param id id of this pending reply reference in the database
  * @param data Wrapped pending reply reference data
  * @author Mikko Hilpinen
  * @since 19.10.2023, v0.1
  */
case class PendingReplyReference(id: Int, data: PendingReplyReferenceData) 
	extends StoredModelConvertible[PendingReplyReferenceData] with FromIdFactory[Int, PendingReplyReference] 
		with PendingReplyReferenceFactoryWrapper[PendingReplyReferenceData, PendingReplyReference]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this pending reply reference in the database
	  */
	def access = DbSinglePendingReplyReference(id)
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory = data
	
	override def withId(id: Int) = copy(id = id)
	
	override protected def wrap(data: PendingReplyReferenceData) = copy(data = data)
}

