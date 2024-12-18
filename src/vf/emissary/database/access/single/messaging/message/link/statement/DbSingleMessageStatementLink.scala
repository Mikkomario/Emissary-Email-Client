package vf.emissary.database.access.single.messaging.message.link.statement

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.stored.messaging.MessageStatementLink

/**
  * An access point to individual message statement links, based on their id
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
case class DbSingleMessageStatementLink(id: Int) 
	extends UniqueMessageStatementLinkAccess with SingleIntIdModelAccess[MessageStatementLink]

