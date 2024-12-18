package vf.emissary.database.access.many.messaging.message.link.statement

import utopia.vault.nosql.view.{UnconditionalView, ViewManyByIntIds}

/**
  * The root access point when targeting multiple message statement links at a time
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object DbMessageStatementLinks 
	extends ManyMessageStatementLinksAccess with UnconditionalView 
		with ViewManyByIntIds[ManyMessageStatementLinksAccess]

