package vf.emissary.database.access.single.messaging.address

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.view.{FilterableView, ViewFactory}
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.AddressDbFactory
import vf.emissary.model.stored.messaging.Address

object UniqueAddressAccess extends ViewFactory[UniqueAddressAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): UniqueAddressAccess = _UniqueAddressAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _UniqueAddressAccess(override val accessCondition: Option[Condition]) 
		extends UniqueAddressAccess
}

/**
  * A common trait for access points that return individual and distinct addresses.
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait UniqueAddressAccess 
	extends UniqueAddressAccessLike[Address, UniqueAddressAccess] with SingleRowModelAccess[Address] 
		with FilterableView[UniqueAddressAccess]
{
	// IMPLEMENTED	--------------------
	
	override def factory = AddressDbFactory
	override protected def self = this
	
	override def apply(condition: Condition): UniqueAddressAccess = UniqueAddressAccess(condition)
}

