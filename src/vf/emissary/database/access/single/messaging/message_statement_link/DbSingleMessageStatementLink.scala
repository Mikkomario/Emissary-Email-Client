package vf.emissary.database.access.single.messaging.message_statement_link

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.stored.messaging.MessageStatementLink

/**
  * An access point to individual message statement links, based on their id
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class DbSingleMessageStatementLink(id: Int) 
	extends UniqueMessageStatementLinkAccess with SingleIntIdModelAccess[MessageStatementLink]

