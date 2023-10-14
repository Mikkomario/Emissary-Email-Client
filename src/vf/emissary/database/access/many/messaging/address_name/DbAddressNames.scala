package vf.emissary.database.access.many.messaging.address_name

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple address names at a time
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
object DbAddressNames extends ManyAddressNamesAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted address names
	  * @return An access point to address names with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbAddressNamesSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbAddressNamesSubset(targetIds: Set[Int]) extends ManyAddressNamesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

