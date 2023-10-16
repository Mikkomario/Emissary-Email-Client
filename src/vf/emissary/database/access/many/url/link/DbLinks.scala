package vf.emissary.database.access.many.url.link

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple links at a time
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
object DbLinks extends ManyLinksAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted links
	  * @return An access point to links with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbLinksSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbLinksSubset(targetIds: Set[Int]) extends ManyLinksAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

