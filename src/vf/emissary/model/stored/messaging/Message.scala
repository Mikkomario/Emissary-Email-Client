package vf.emissary.model.stored.messaging

import utopia.vault.model.template.StoredModelConvertible
import vf.emissary.database.access.single.messaging.message.DbSingleMessage
import vf.emissary.model.partial.messaging.MessageData

/**
  * Represents a message that has already been stored in the database
  * @param id id of this message in the database
  * @param data Wrapped message data
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class Message(id: Int, data: MessageData) extends StoredModelConvertible[MessageData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this message in the database
	  */
	def access = DbSingleMessage(id)
}

