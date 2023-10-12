package vf.emissary.model.stored.messaging

import utopia.vault.model.template.StoredModelConvertible
import vf.emissary.database.access.single.messaging.message_statement_link.DbSingleMessageStatementLink
import vf.emissary.model.partial.messaging.MessageStatementLinkData

/**
  * Represents a message statement link that has already been stored in the database
  * @param id id of this message statement link in the database
  * @param data Wrapped message statement link data
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class MessageStatementLink(id: Int, data: MessageStatementLinkData) 
	extends StoredModelConvertible[MessageStatementLinkData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this message statement link in the database
	  */
	def access = DbSingleMessageStatementLink(id)
}

