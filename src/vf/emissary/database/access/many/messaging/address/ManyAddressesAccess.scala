package vf.emissary.database.access.many.messaging.address

import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.AddressFactory
import vf.emissary.model.stored.messaging.Address

object ManyAddressesAccess
{
	// NESTED	--------------------
	
	private class ManyAddressesSubView(condition: Condition) extends ManyAddressesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple addresses at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait ManyAddressesAccess 
	extends ManyAddressesAccessLike[Address, ManyAddressesAccess] with ManyRowModelAccess[Address]
{
	// IMPLEMENTED	--------------------
	
	override def factory = AddressFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyAddressesAccess = 
		new ManyAddressesAccess.ManyAddressesSubView(mergeCondition(filterCondition))
}

