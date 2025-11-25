package vf.emissary.database.access.many.messaging.service.user

import utopia.vault.nosql.factory.FromResultFactory
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.DetailedEmailServiceUserDbFactory
import vf.emissary.model.combined.messaging.DetailedEmailServiceUser

object ManyDetailedEmailServiceUsersAccess
{
	// OTHER    ---------------------------
	
	/**
	 * @param condition Search condition to apply
	 * @return Access to users fulfilling the specified search condition
	 */
	def apply(condition: Condition): ManyDetailedEmailServiceUsersAccess = _Access(Some(condition))
	
	
	// NESTED   ---------------------------
	
	private case class _Access(accessCondition: Option[Condition]) extends ManyDetailedEmailServiceUsersAccess
}

/**
 * Common trait for access points targeting email service users and their related information
 *
 * @author Mikko Hilpinen
 * @since 22.12.2024, v1.1
 */
trait ManyDetailedEmailServiceUsersAccess
	extends ManyEmailServiceUsersAccessLike[DetailedEmailServiceUser, ManyDetailedEmailServiceUsersAccess]
{
	override def self: ManyDetailedEmailServiceUsersAccess = this
	override def factory: FromResultFactory[DetailedEmailServiceUser] = DetailedEmailServiceUserDbFactory
	
	override def apply(condition: Condition): ManyDetailedEmailServiceUsersAccess =
		ManyDetailedEmailServiceUsersAccess(condition)
}
