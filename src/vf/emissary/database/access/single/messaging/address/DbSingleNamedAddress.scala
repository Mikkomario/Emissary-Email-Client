package vf.emissary.database.access.single.messaging.address

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.combined.messaging.NamedAddress

/**
  * An access point to individual named addressses, based on their address id
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
case class DbSingleNamedAddress(id: Int) 
	extends UniqueNamedAddressAccess with SingleIntIdModelAccess[NamedAddress]

