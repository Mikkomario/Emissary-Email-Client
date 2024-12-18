package vf.emissary.database.access.many.messaging.thread.link.subject

import utopia.vault.nosql.view.{UnconditionalView, ViewManyByIntIds}

/**
  * The root access point when targeting multiple message thread subject links at a time
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object DbMessageThreadSubjectLinks 
	extends ManyMessageThreadSubjectLinksAccess with UnconditionalView 
		with ViewManyByIntIds[ManyMessageThreadSubjectLinksAccess]

