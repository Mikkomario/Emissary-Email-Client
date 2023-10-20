package vf.emissary.model.stored.messaging

import utopia.vault.model.template.StoredModelConvertible
import vf.emissary.database.access.single.messaging.pending_thread_reference.DbSinglePendingThreadReference
import vf.emissary.model.partial.messaging.PendingThreadReferenceData

/**
  * Represents a pending thread reference that has already been stored in the database
  * @param id id of this pending thread reference in the database
  * @param data Wrapped pending thread reference data
  * @author Mikko Hilpinen
  * @since 19.10.2023, v0.1
  */
case class PendingThreadReference(id: Int, data: PendingThreadReferenceData) 
	extends StoredModelConvertible[PendingThreadReferenceData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this pending thread reference in the database
	  */
	def access = DbSinglePendingThreadReference(id)
}

