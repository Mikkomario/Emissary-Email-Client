package vf.emissary.database.access.many.messaging.attachment.link.message

import utopia.vault.nosql.view.{UnconditionalView, ViewManyByIntIds}

/**
  * The root access point when targeting multiple attachment message links at a time
  * @author Mikko Hilpinen
  * @since 27.12.2024, v1.1
  */
object DbAttachmentMessageLinks 
	extends ManyAttachmentMessageLinksAccess with UnconditionalView 
		with ViewManyByIntIds[ManyAttachmentMessageLinksAccess]

