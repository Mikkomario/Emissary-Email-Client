package vf.emissary.database.access.many.text.statement

import utopia.vault.nosql.view.{UnconditionalView, ViewManyByIntIds}

/**
  * The root access point when targeting multiple subject statements at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbSubjectStatements
	extends ManySubjectStatementsAccess with UnconditionalView with ViewManyByIntIds[ManySubjectStatementsAccess]
