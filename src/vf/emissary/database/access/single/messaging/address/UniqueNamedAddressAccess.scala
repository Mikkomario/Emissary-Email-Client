package vf.emissary.database.access.single.messaging.address

import utopia.vault.nosql.view.{FilterableView, ViewFactory}
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.NamedAddressDbFactory
import vf.emissary.database.storable.messaging.AddressNameDbModel
import vf.emissary.model.combined.messaging.NamedAddress

object UniqueNamedAddressAccess extends ViewFactory[UniqueNamedAddressAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): UniqueNamedAddressAccess = 
		_UniqueNamedAddressAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _UniqueNamedAddressAccess(override val accessCondition: Option[Condition]) 
		extends UniqueNamedAddressAccess
}

/**
  * A common trait for access points that return distinct named addresses
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
trait UniqueNamedAddressAccess extends UniqueAddressAccessLike[NamedAddress, UniqueNamedAddressAccess]
{
	// COMPUTED	--------------------
	
	/**
	  * A database model (factory) used for interacting with the linked names
	  */
	protected def nameModel = AddressNameDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = NamedAddressDbFactory
	override def self = this
	
	override def apply(condition: Condition): UniqueNamedAddressAccess = UniqueNamedAddressAccess(condition)
}

