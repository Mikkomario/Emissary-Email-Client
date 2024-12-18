package vf.emissary.database.access.single.messaging.reference.pending.thread

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.stored.messaging.PendingThreadReference

/**
  * An access point to individual pending thread references, based on their id
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
case class DbSinglePendingThreadReference(id: Int) 
	extends UniquePendingThreadReferenceAccess with SingleIntIdModelAccess[PendingThreadReference]

