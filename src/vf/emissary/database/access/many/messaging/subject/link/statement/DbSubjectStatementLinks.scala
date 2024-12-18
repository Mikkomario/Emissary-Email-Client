package vf.emissary.database.access.many.messaging.subject.link.statement

import utopia.vault.nosql.view.{UnconditionalView, ViewManyByIntIds}

/**
  * The root access point when targeting multiple subject statement links at a time
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object DbSubjectStatementLinks 
	extends ManySubjectStatementLinksAccess with UnconditionalView 
		with ViewManyByIntIds[ManySubjectStatementLinksAccess]

