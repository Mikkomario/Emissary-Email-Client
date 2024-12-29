package vf.emissary.database.access.single.messaging.attachment

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.combined.messaging.MessageAttachment

/**
  * An access point to individual message attachments, based on their attachment id
  * @author Mikko Hilpinen
  * @since 27.12.2024, v1.1
  */
case class DbSingleMessageAttachment(id: Int) 
	extends UniqueMessageAttachmentAccess with SingleIntIdModelAccess[MessageAttachment]

