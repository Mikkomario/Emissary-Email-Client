package vf.emissary.model.stored.messaging

import utopia.flow.generic.model.template.ModelLike.AnyModel
import utopia.vault.model.template.{FromIdFactory, StoredFromModelFactory, StoredModelConvertible}
import vf.emissary.database.access.single.messaging.reference.pending.thread.DbSinglePendingThreadReference
import vf.emissary.model.factory.messaging.PendingThreadReferenceFactoryWrapper
import vf.emissary.model.partial.messaging.PendingThreadReferenceData

object PendingThreadReference 
	extends StoredFromModelFactory[PendingThreadReferenceData, PendingThreadReference]
{
	// IMPLEMENTED	--------------------
	
	override def dataFactory = PendingThreadReferenceData
	
	override protected def complete(model: AnyModel, data: PendingThreadReferenceData) = 
		model("id").tryInt.map { apply(_, data) }
}

/**
  * Represents a pending thread reference that has already been stored in the database
  * @param id id of this pending thread reference in the database
  * @param data Wrapped pending thread reference data
  * @author Mikko Hilpinen
  * @since 19.10.2023, v0.1
  */
case class PendingThreadReference(id: Int, data: PendingThreadReferenceData) 
	extends StoredModelConvertible[PendingThreadReferenceData] 
		with FromIdFactory[Int, PendingThreadReference] 
		with PendingThreadReferenceFactoryWrapper[PendingThreadReferenceData, PendingThreadReference]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this pending thread reference in the database
	  */
	def access = DbSinglePendingThreadReference(id)
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory = data
	
	override def withId(id: Int) = copy(id = id)
	
	override protected def wrap(data: PendingThreadReferenceData) = copy(data = data)
}

