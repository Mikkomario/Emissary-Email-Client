package vf.emissary.database.access.many.messaging.subject

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple thread subjects at a time
  * @author Mikko Hilpinen
  * @since 17.10.2023, v0.1
  */
object DbThreadSubjects extends ManyThreadSubjectsAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted thread subjects
	  * @return An access point to thread subjects with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbThreadSubjectsSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbThreadSubjectsSubset(targetIds: Set[Int]) extends ManyThreadSubjectsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

