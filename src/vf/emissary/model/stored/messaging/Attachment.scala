package vf.emissary.model.stored.messaging

import utopia.vault.model.template.StoredModelConvertible
import vf.emissary.database.access.single.messaging.attachment.DbSingleAttachment
import vf.emissary.model.partial.messaging.AttachmentData

/**
  * Represents a attachment that has already been stored in the database
  * @param id id of this attachment in the database
  * @param data Wrapped attachment data
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
case class Attachment(id: Int, data: AttachmentData) extends StoredModelConvertible[AttachmentData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this attachment in the database
	  */
	def access = DbSingleAttachment(id)
}

