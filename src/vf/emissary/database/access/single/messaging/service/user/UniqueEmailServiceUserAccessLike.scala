package vf.emissary.database.access.single.messaging.service.user

import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleModelAccess
import utopia.vault.nosql.access.single.model.distinct.UniqueModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import vf.emissary.database.storable.messaging.EmailServiceUserDbModel

/**
  * A common trait for access points which target individual email service users or similar items at a time
  * @tparam A Type of read (email service users -like) instances
  * @tparam Repr Type of this access point
  * @author Mikko Hilpinen
  * @since 22.12.2024, v1.1
  */
trait UniqueEmailServiceUserAccessLike[+A, +Repr] extends UniqueModelAccess[A] with FilterableView[Repr] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the used emailing service. 
	  * None if no email service user (or value) was found.
	  */
	def serviceId(implicit connection: Connection) = pullColumn(model.serviceId.column).int
	/**
	  * Email address that represents this user. 
	  * None if no email service user (or value) was found.
	  */
	def addressId(implicit connection: Connection) = pullColumn(model.addressId.column).int
	/**
	  * Password used for authenticating to the email service. Empty if password should be provided 
	  * externally. 
	  * None if no email service user (or value) was found.
	  */
	def password(implicit connection: Connection) = pullColumn(model.password.column).getString
	/**
	  * Time when this email service user was added to the database. 
	  * None if no email service user (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.created.column).instant
	/**
	  * Unique id of the accessible email service user. None if no email service user was accessible.
	  */
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = EmailServiceUserDbModel
}

