package vf.emissary.database.access.many.messaging.message

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple messages at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbMessages extends ManyMessagesAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted messages
	  * @return An access point to messages with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbMessagesSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbMessagesSubset(targetIds: Set[Int]) extends ManyMessagesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(index in targetIds)
	}
}

