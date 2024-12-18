package vf.emissary.database.access.single.messaging.thread

import utopia.vault.nosql.access.single.model.SingleChronoRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.MessageThreadDbFactory
import vf.emissary.model.stored.messaging.MessageThread

object UniqueMessageThreadAccess extends ViewFactory[UniqueMessageThreadAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): UniqueMessageThreadAccess = 
		_UniqueMessageThreadAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _UniqueMessageThreadAccess(override val accessCondition: Option[Condition]) 
		extends UniqueMessageThreadAccess
}

/**
  * A common trait for access points that return individual and distinct message threads.
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait UniqueMessageThreadAccess 
	extends UniqueMessageThreadAccessLike[MessageThread, UniqueMessageThreadAccess] 
		with SingleChronoRowModelAccess[MessageThread, UniqueMessageThreadAccess]
{
	// IMPLEMENTED	--------------------
	
	override def factory = MessageThreadDbFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): UniqueMessageThreadAccess = UniqueMessageThreadAccess(condition)
}

