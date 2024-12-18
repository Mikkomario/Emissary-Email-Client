package vf.emissary.database.access.many.text.statement

import utopia.vault.nosql.view.{UnconditionalView, ViewManyByIntIds}

/**
  * The root access point when targeting multiple message statements at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbMessageStatements
	extends ManyMessageStatementsAccess with UnconditionalView with ViewManyByIntIds[ManyMessageStatementsAccess]

