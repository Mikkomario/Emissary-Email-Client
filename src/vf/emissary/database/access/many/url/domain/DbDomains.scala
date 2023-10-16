package vf.emissary.database.access.many.url.domain

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple domains at a time
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
object DbDomains extends ManyDomainsAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted domains
	  * @return An access point to domains with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbDomainsSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbDomainsSubset(targetIds: Set[Int]) extends ManyDomainsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

