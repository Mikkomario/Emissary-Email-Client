package vf.emissary.database.access.many.messaging.attachment

import utopia.vault.nosql.view.{UnconditionalView, ViewManyByIntIds}

/**
  * The root access point when targeting multiple message attachments at a time
  * @author Mikko Hilpinen
  * @since 27.12.2024, v1.1
  */
object DbMessageAttachments 
	extends ManyMessageAttachmentsAccess with UnconditionalView 
		with ViewManyByIntIds[ManyMessageAttachmentsAccess]

