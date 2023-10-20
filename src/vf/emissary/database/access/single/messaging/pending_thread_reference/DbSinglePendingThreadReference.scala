package vf.emissary.database.access.single.messaging.pending_thread_reference

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.stored.messaging.PendingThreadReference

/**
  * An access point to individual pending thread references, based on their id
  * @author Mikko Hilpinen
  * @since 19.10.2023, v0.1
  */
case class DbSinglePendingThreadReference(id: Int) 
	extends UniquePendingThreadReferenceAccess with SingleIntIdModelAccess[PendingThreadReference]

