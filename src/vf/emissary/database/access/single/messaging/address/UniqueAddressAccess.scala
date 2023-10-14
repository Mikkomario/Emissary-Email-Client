package vf.emissary.database.access.single.messaging.address

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.AddressFactory
import vf.emissary.model.stored.messaging.Address

object UniqueAddressAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueAddressAccess = new _UniqueAddressAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueAddressAccess(condition: Condition) extends UniqueAddressAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct addresses.
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait UniqueAddressAccess 
	extends UniqueAddressAccessLike[Address] with SingleRowModelAccess[Address] with FilterableView[UniqueAddressAccess]
{
	// IMPLEMENTED	--------------------
	
	override def factory = AddressFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueAddressAccess = 
		new UniqueAddressAccess._UniqueAddressAccess(mergeCondition(filterCondition))
}

