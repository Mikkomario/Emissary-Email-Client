package vf.emissary.database.access.single.messaging.message

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.stored.messaging.Message

/**
  * An access point to individual messages, based on their id
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class DbSingleMessage(id: Int) extends UniqueMessageAccess with SingleIntIdModelAccess[Message]

