package vf.emissary.database.access.many.messaging.reference.pending.reply

import utopia.vault.nosql.view.{UnconditionalView, ViewManyByIntIds}

/**
  * The root access point when targeting multiple pending reply references at a time
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object DbPendingReplyReferences 
	extends ManyPendingReplyReferencesAccess with UnconditionalView 
		with ViewManyByIntIds[ManyPendingReplyReferencesAccess]

