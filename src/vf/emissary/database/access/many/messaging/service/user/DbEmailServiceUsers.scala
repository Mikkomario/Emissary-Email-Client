package vf.emissary.database.access.many.messaging.service.user

import utopia.vault.nosql.view.{UnconditionalView, ViewManyByIntIds}

/**
  * The root access point when targeting multiple email service users at a time
  * @author Mikko Hilpinen
  * @since 22.12.2024, v1.1
  */
object DbEmailServiceUsers 
	extends ManyEmailServiceUsersAccess with UnconditionalView 
		with ViewManyByIntIds[ManyEmailServiceUsersAccess]

