package vf.emissary.database.access.many.messaging.service.user

import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.EmailServiceUserDbFactory
import vf.emissary.model.stored.messaging.EmailServiceUser

object ManyEmailServiceUsersAccess extends ViewFactory[ManyEmailServiceUsersAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyEmailServiceUsersAccess = 
		_ManyEmailServiceUsersAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyEmailServiceUsersAccess(override val accessCondition: Option[Condition]) 
		extends ManyEmailServiceUsersAccess
}

/**
  * A common trait for access points which target multiple email service users at a time
  * @author Mikko Hilpinen
  * @since 22.12.2024, v1.1
  */
trait ManyEmailServiceUsersAccess 
	extends ManyEmailServiceUsersAccessLike[EmailServiceUser, ManyEmailServiceUsersAccess] 
		with ManyRowModelAccess[EmailServiceUser]
{
	// COMPUTED	--------------------
	
	/**
	  * Copy of this access point which includes related information
	  */
	def detailed = DbDetailedEmailServiceUsers.filter(accessCondition)
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = EmailServiceUserDbFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): ManyEmailServiceUsersAccess = 
		ManyEmailServiceUsersAccess(condition)
}

