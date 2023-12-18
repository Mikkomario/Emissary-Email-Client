package vf.emissary.database.access.single.messaging.address

import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.NamedAddressFactory
import vf.emissary.database.model.messaging.AddressNameModel
import vf.emissary.model.combined.messaging.NamedAddress

object UniqueNamedAddressAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueNamedAddressAccess = new _UniqueNamedAddressAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueNamedAddressAccess(condition: Condition) extends UniqueNamedAddressAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return distinct named addressses
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
trait UniqueNamedAddressAccess 
	extends UniqueAddressAccessLike[NamedAddress] with FilterableView[UniqueNamedAddressAccess]
{
	// COMPUTED	--------------------
	
	/**
	  * A database model (factory) used for interacting with the linked names
	  */
	protected def nameModel = AddressNameModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = NamedAddressFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueNamedAddressAccess = 
		new UniqueNamedAddressAccess._UniqueNamedAddressAccess(mergeCondition(filterCondition))
}

