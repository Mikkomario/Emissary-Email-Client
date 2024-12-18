package vf.emissary.database.access.many.messaging.reference.pending.thread

import utopia.vault.nosql.view.{UnconditionalView, ViewManyByIntIds}

/**
  * The root access point when targeting multiple pending thread references at a time
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object DbPendingThreadReferences 
	extends ManyPendingThreadReferencesAccess with UnconditionalView 
		with ViewManyByIntIds[ManyPendingThreadReferencesAccess]

