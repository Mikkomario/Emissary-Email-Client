package vf.emissary.database.access.many.messaging.thread

import utopia.vault.nosql.view.{UnconditionalView, ViewManyByIntIds}

/**
  * The root access point when targeting multiple message threads at a time
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object DbMessageThreads 
	extends ManyMessageThreadsAccess with UnconditionalView with ViewManyByIntIds[ManyMessageThreadsAccess]

