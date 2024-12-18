package vf.emissary.database.access.single.messaging.reference.pending.reply

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.stored.messaging.PendingReplyReference

/**
  * An access point to individual pending reply references, based on their id
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
case class DbSinglePendingReplyReference(id: Int) 
	extends UniquePendingReplyReferenceAccess with SingleIntIdModelAccess[PendingReplyReference]

