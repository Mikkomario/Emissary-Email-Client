package vf.emissary.database.factory.messaging

import utopia.vault.nosql.factory.multi.MultiCombiningFactory
import vf.emissary.model.combined.messaging.NamedAddress
import vf.emissary.model.stored.messaging.{Address, AddressName}

/**
  * Used for reading named addresses from the database
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object NamedAddressDbFactory extends MultiCombiningFactory[NamedAddress, Address, AddressName]
{
	// IMPLEMENTED	--------------------
	
	override def childFactory = AddressNameDbFactory
	
	override def isAlwaysLinked = false
	
	override def parentFactory = AddressDbFactory
	
	/**
	  * @param address address to wrap
	  * @param names names to attach to this address
	  */
	override def apply(address: Address, names: Seq[AddressName]) = NamedAddress(address, names)
}

