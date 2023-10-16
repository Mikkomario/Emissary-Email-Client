package vf.emissary.database.access.single.messaging.message_recipient_link

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.stored.messaging.MessageRecipientLink

/**
  * An access point to individual message recipient links, based on their id
  * @author Mikko Hilpinen
  * @since 15.10.2023, v0.1
  */
case class DbSingleMessageRecipientLink(id: Int) 
	extends UniqueMessageRecipientLinkAccess with SingleIntIdModelAccess[MessageRecipientLink]

