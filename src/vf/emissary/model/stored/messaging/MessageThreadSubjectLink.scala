package vf.emissary.model.stored.messaging

import utopia.vault.model.template.StoredModelConvertible
import vf.emissary.database.access.single.messaging.message_thread_subject_link.DbSingleMessageThreadSubjectLink
import vf.emissary.model.partial.messaging.MessageThreadSubjectLinkData

/**
  * Represents a message thread subject link that has already been stored in the database
  * @param id id of this message thread subject link in the database
  * @param data Wrapped message thread subject link data
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class MessageThreadSubjectLink(id: Int, data: MessageThreadSubjectLinkData) 
	extends StoredModelConvertible[MessageThreadSubjectLinkData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this message thread subject link in the database
	  */
	def access = DbSingleMessageThreadSubjectLink(id)
}

