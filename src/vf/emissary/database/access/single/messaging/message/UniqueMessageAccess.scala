package vf.emissary.database.access.single.messaging.message

import utopia.vault.nosql.access.single.model.SingleChronoRowModelAccess
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.MessageFactory
import vf.emissary.model.stored.messaging.Message

object UniqueMessageAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueMessageAccess = new _UniqueMessageAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueMessageAccess(condition: Condition) extends UniqueMessageAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct messages.
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait UniqueMessageAccess 
	extends UniqueMessageAccessLike[Message] with SingleChronoRowModelAccess[Message, UniqueMessageAccess]
{
	// IMPLEMENTED	--------------------
	
	override def factory = MessageFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueMessageAccess = 
		new UniqueMessageAccess._UniqueMessageAccess(mergeCondition(filterCondition))
}

