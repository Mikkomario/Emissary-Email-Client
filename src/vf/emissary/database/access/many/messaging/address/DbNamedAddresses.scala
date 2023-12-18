package vf.emissary.database.access.many.messaging.address

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple named addressses at a time
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
object DbNamedAddresses extends ManyNamedAddressesAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted named addressses
	  * @return An access point to named addressses with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbNamedAddressesSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbNamedAddressesSubset(targetIds: Set[Int]) extends ManyNamedAddressesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(index in targetIds)
	}
}

