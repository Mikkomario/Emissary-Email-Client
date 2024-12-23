package vf.emissary.database.access.many.messaging.service

import utopia.vault.nosql.view.{UnconditionalView, ViewManyByIntIds}

/**
  * The root access point when targeting multiple email services at a time
  * @author Mikko Hilpinen
  * @since 22.12.2024, v1.1
  */
object DbEmailServices 
	extends ManyEmailServicesAccess with UnconditionalView with ViewManyByIntIds[ManyEmailServicesAccess]

