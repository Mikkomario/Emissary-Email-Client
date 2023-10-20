package vf.emissary.database.access.many.messaging.pending_thread_reference

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple pending thread references at a time
  * @author Mikko Hilpinen
  * @since 19.10.2023, v0.1
  */
object DbPendingThreadReferences extends ManyPendingThreadReferencesAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted pending thread references
	  * @return An access point to pending thread references with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbPendingThreadReferencesSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbPendingThreadReferencesSubset(targetIds: Set[Int]) extends ManyPendingThreadReferencesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

