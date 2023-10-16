package vf.emissary.database.access.many.url.request_path

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple request paths at a time
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
object DbRequestPaths extends ManyRequestPathsAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted request paths
	  * @return An access point to request paths with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbRequestPathsSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbRequestPathsSubset(targetIds: Set[Int]) extends ManyRequestPathsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

