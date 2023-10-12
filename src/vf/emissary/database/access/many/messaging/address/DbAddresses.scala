package vf.emissary.database.access.many.messaging.address

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple addresses at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbAddresses extends ManyAddressesAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted addresses
	  * @return An access point to addresses with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbAddressesSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbAddressesSubset(targetIds: Set[Int]) extends ManyAddressesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

