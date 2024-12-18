package vf.emissary.database.access.single.messaging.thread

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.stored.messaging.MessageThread

/**
  * An access point to individual message threads, based on their id
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
case class DbSingleMessageThread(id: Int) 
	extends UniqueMessageThreadAccess with SingleIntIdModelAccess[MessageThread]

