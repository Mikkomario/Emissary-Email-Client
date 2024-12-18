package vf.emissary.database.access.many.messaging.message.link.recipient

import utopia.vault.nosql.view.{UnconditionalView, ViewManyByIntIds}

/**
  * The root access point when targeting multiple message recipient links at a time
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object DbMessageRecipientLinks 
	extends ManyMessageRecipientLinksAccess with UnconditionalView 
		with ViewManyByIntIds[ManyMessageRecipientLinksAccess]

