package vf.emissary.database.access.single.messaging.address

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.AddressFactory
import vf.emissary.database.model.messaging.AddressModel
import vf.emissary.model.stored.messaging.Address

/**
  * Used for accessing individual addresses
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbAddress extends SingleRowModelAccess[Address] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = AddressModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = AddressFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted address
	  * @return An access point to that address
	  */
	def apply(id: Int) = DbSingleAddress(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique addresses.
	  * @return An access point to the address that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = UniqueAddressAccess(mergeCondition(condition))
}

