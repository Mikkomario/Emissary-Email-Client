package vf.emissary.database.access.single.messaging.address_name

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.stored.messaging.AddressName

/**
  * An access point to individual address names, based on their id
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
case class DbSingleAddressName(id: Int) 
	extends UniqueAddressNameAccess with SingleIntIdModelAccess[AddressName]

