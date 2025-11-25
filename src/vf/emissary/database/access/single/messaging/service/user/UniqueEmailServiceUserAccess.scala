package vf.emissary.database.access.single.messaging.service.user

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.EmailServiceUserDbFactory
import vf.emissary.model.stored.messaging.EmailServiceUser

object UniqueEmailServiceUserAccess extends ViewFactory[UniqueEmailServiceUserAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): UniqueEmailServiceUserAccess = _UniqueEmailServiceUserAccess(condition)
	
	
	// NESTED	--------------------
	
	private case class _UniqueEmailServiceUserAccess(override val condition: Condition) 
		extends UniqueEmailServiceUserAccess
}

/**
  * A common trait for access points that return individual and distinct email service users.
  * @author Mikko Hilpinen
  * @since 22.12.2024, v1.1
  */
trait UniqueEmailServiceUserAccess 
	extends UniqueEmailServiceUserAccessLike[EmailServiceUser, UniqueEmailServiceUserAccess] 
		with SingleRowModelAccess[EmailServiceUser]
{
	// IMPLEMENTED	--------------------
	
	override def factory = EmailServiceUserDbFactory
	override def self = this
	
	override def apply(condition: Condition): UniqueEmailServiceUserAccess = 
		UniqueEmailServiceUserAccess(condition)
}

