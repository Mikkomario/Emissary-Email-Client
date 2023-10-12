package vf.emissary.database.access.single.messaging.message_thread

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.stored.messaging.MessageThread

/**
  * An access point to individual message threads, based on their id
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class DbSingleMessageThread(id: Int) 
	extends UniqueMessageThreadAccess with SingleIntIdModelAccess[MessageThread]

