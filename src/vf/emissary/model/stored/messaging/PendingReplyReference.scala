package vf.emissary.model.stored.messaging

import utopia.vault.model.template.StoredModelConvertible
import vf.emissary.database.access.single.messaging.pending_reply_reference.DbSinglePendingReplyReference
import vf.emissary.model.partial.messaging.PendingReplyReferenceData

/**
  * Represents a pending reply reference that has already been stored in the database
  * @param id id of this pending reply reference in the database
  * @param data Wrapped pending reply reference data
  * @author Mikko Hilpinen
  * @since 19.10.2023, v0.1
  */
case class PendingReplyReference(id: Int, data: PendingReplyReferenceData) 
	extends StoredModelConvertible[PendingReplyReferenceData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this pending reply reference in the database
	  */
	def access = DbSinglePendingReplyReference(id)
}

