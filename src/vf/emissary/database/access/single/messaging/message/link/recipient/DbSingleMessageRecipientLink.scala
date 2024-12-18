package vf.emissary.database.access.single.messaging.message.link.recipient

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.stored.messaging.MessageRecipientLink

/**
  * An access point to individual message recipient links, based on their id
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
case class DbSingleMessageRecipientLink(id: Int) 
	extends UniqueMessageRecipientLinkAccess with SingleIntIdModelAccess[MessageRecipientLink]

