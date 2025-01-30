package vf.emissary.database.access.single.messaging.message

import utopia.vault.nosql.access.single.model.SingleChronoRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.MessageDbFactory
import vf.emissary.model.stored.messaging.StoredMessage

object UniqueMessageAccess extends ViewFactory[UniqueMessageAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): UniqueMessageAccess = _UniqueMessageAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _UniqueMessageAccess(override val accessCondition: Option[Condition]) 
		extends UniqueMessageAccess
}

/**
  * A common trait for access points that return individual and distinct messages.
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait UniqueMessageAccess 
	extends UniqueMessageAccessLike[StoredMessage, UniqueMessageAccess]
		with SingleChronoRowModelAccess[StoredMessage, UniqueMessageAccess]
{
	// IMPLEMENTED	--------------------
	
	override def factory = MessageDbFactory
	override protected def self = this
	
	override def apply(condition: Condition): UniqueMessageAccess = UniqueMessageAccess(condition)
}

