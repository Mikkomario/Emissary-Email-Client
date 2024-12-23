package vf.emissary.database.access.many.messaging.service.user

import utopia.vault.nosql.view.{UnconditionalView, ViewManyByIntIds}

/**
 * Root access point to email service users, including related information
 *
 * @author Mikko Hilpinen
 * @since 22.12.2024, v1.1
 */
object DbDetailedEmailServiceUsers
	extends ManyDetailedEmailServiceUsersAccess with UnconditionalView
		with ViewManyByIntIds[ManyDetailedEmailServiceUsersAccess]
