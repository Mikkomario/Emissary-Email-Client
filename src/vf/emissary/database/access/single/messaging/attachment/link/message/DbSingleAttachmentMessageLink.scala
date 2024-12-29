package vf.emissary.database.access.single.messaging.attachment.link.message

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.stored.messaging.AttachmentMessageLink

/**
  * An access point to individual attachment message links, based on their id
  * @author Mikko Hilpinen
  * @since 27.12.2024, v1.1
  */
case class DbSingleAttachmentMessageLink(id: Int) 
	extends UniqueAttachmentMessageLinkAccess with SingleIntIdModelAccess[AttachmentMessageLink]

