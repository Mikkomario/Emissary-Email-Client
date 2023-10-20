package vf.emissary.database.access.single.messaging.pending_reply_reference

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.stored.messaging.PendingReplyReference

/**
  * An access point to individual pending reply references, based on their id
  * @author Mikko Hilpinen
  * @since 19.10.2023, v0.1
  */
case class DbSinglePendingReplyReference(id: Int) 
	extends UniquePendingReplyReferenceAccess with SingleIntIdModelAccess[PendingReplyReference]

