package vf.emissary.database.access.single.messaging.message_thread

import utopia.vault.nosql.access.single.model.SingleChronoRowModelAccess
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.MessageThreadFactory
import vf.emissary.model.stored.messaging.MessageThread

object UniqueMessageThreadAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueMessageThreadAccess = new _UniqueMessageThreadAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueMessageThreadAccess(condition: Condition) extends UniqueMessageThreadAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct message threads.
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait UniqueMessageThreadAccess 
	extends UniqueMessageThreadAccessLike[MessageThread] 
		with SingleChronoRowModelAccess[MessageThread, UniqueMessageThreadAccess]
{
	// IMPLEMENTED	--------------------
	
	override def factory = MessageThreadFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueMessageThreadAccess = 
		new UniqueMessageThreadAccess._UniqueMessageThreadAccess(mergeCondition(filterCondition))
}

