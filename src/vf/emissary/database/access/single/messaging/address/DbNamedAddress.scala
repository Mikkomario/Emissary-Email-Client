package vf.emissary.database.access.single.messaging.address

import utopia.vault.nosql.access.single.model.SingleModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.NamedAddressFactory
import vf.emissary.database.model.messaging.{AddressModel, AddressNameModel}
import vf.emissary.model.combined.messaging.NamedAddress

/**
  * Used for accessing individual named addressses
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
object DbNamedAddress extends SingleModelAccess[NamedAddress] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * A database model (factory) used for interacting with linked addresses
	  */
	protected def model = AddressModel
	
	/**
	  * A database model (factory) used for interacting with the linked names
	  */
	protected def nameModel = AddressNameModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = NamedAddressFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted named address
	  * @return An access point to that named address
	  */
	def apply(id: Int) = DbSingleNamedAddress(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique named addressses.
	  * @return An access point to the named address that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = UniqueNamedAddressAccess(mergeCondition(condition))
}

