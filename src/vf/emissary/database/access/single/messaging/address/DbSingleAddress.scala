package vf.emissary.database.access.single.messaging.address

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.stored.messaging.Address

/**
  * An access point to individual addresses, based on their id
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class DbSingleAddress(id: Int) extends UniqueAddressAccess with SingleIntIdModelAccess[Address]

