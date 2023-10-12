package vf.emissary.database.access.many.messaging.subject

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple subjects at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbSubjects extends ManySubjectsAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted subjects
	  * @return An access point to subjects with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbSubjectsSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbSubjectsSubset(targetIds: Set[Int]) extends ManySubjectsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

