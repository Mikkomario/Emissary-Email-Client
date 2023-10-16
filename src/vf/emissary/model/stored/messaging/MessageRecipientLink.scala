package vf.emissary.model.stored.messaging

import utopia.vault.model.template.StoredModelConvertible
import vf.emissary.database.access.single.messaging.message_recipient_link.DbSingleMessageRecipientLink
import vf.emissary.model.partial.messaging.MessageRecipientLinkData

/**
  * Represents a message recipient link that has already been stored in the database
  * @param id id of this message recipient link in the database
  * @param data Wrapped message recipient link data
  * @author Mikko Hilpinen
  * @since 15.10.2023, v0.1
  */
case class MessageRecipientLink(id: Int, data: MessageRecipientLinkData) 
	extends StoredModelConvertible[MessageRecipientLinkData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this message recipient link in the database
	  */
	def access = DbSingleMessageRecipientLink(id)
}

