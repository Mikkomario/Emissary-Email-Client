package vf.emissary.database.access.many.messaging.pending_reply_reference

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple pending reply references at a time
  * @author Mikko Hilpinen
  * @since 19.10.2023, v0.1
  */
object DbPendingReplyReferences extends ManyPendingReplyReferencesAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted pending reply references
	  * @return An access point to pending reply references with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbPendingReplyReferencesSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbPendingReplyReferencesSubset(targetIds: Set[Int]) extends ManyPendingReplyReferencesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

