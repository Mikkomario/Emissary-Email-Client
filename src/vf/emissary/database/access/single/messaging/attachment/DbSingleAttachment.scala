package vf.emissary.database.access.single.messaging.attachment

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.stored.messaging.Attachment

/**
  * An access point to individual attachments, based on their id
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
case class DbSingleAttachment(id: Int) extends UniqueAttachmentAccess with SingleIntIdModelAccess[Attachment]

