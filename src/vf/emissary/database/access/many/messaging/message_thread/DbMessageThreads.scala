package vf.emissary.database.access.many.messaging.message_thread

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple message threads at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbMessageThreads extends ManyMessageThreadsAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted message threads
	  * @return An access point to message threads with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbMessageThreadsSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbMessageThreadsSubset(targetIds: Set[Int]) extends ManyMessageThreadsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

