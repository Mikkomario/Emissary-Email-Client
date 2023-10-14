package vf.emissary.database.factory.messaging

import utopia.vault.nosql.factory.multi.MultiCombiningFactory
import vf.emissary.model.combined.messaging.NamedAddress
import vf.emissary.model.stored.messaging.{Address, AddressName}

/**
  * Used for reading named addressses from the database
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
object NamedAddressFactory extends MultiCombiningFactory[NamedAddress, Address, AddressName]
{
	// IMPLEMENTED	--------------------
	
	override def childFactory = AddressNameFactory
	
	override def isAlwaysLinked = false
	
	override def parentFactory = AddressFactory
	
	override def apply(address: Address, names: Vector[AddressName]) = NamedAddress(address, names)
}

