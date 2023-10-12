package vf.emissary.model.stored.messaging

import utopia.vault.model.template.StoredModelConvertible
import vf.emissary.database.access.single.messaging.message_thread.DbSingleMessageThread
import vf.emissary.model.partial.messaging.MessageThreadData

/**
  * Represents a message thread that has already been stored in the database
  * @param id id of this message thread in the database
  * @param data Wrapped message thread data
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class MessageThread(id: Int, data: MessageThreadData) extends StoredModelConvertible[MessageThreadData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this message thread in the database
	  */
	def access = DbSingleMessageThread(id)
}

