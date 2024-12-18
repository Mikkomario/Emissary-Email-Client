package vf.emissary.database.access.many.messaging.address.name

import utopia.vault.nosql.view.{UnconditionalView, ViewManyByIntIds}

/**
  * The root access point when targeting multiple address names at a time
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object DbAddressNames 
	extends ManyAddressNamesAccess with UnconditionalView with ViewManyByIntIds[ManyAddressNamesAccess]

