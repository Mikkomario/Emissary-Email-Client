package vf.emissary.database.access.many.messaging.service.user

import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import vf.emissary.database.storable.messaging.EmailServiceUserDbModel

/**
  * A common trait for access points which target multiple email service users or similar instances at a time
  * @tparam A Type of read (email service users -like) instances
  * @tparam Repr Type of this access point
  * @author Mikko Hilpinen
  * @since 22.12.2024, v1.1
  */
trait ManyEmailServiceUsersAccessLike[+A, +Repr] 
	extends ManyModelAccess[A] with Indexed with FilterableView[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * service ids of the accessible email service users
	  */
	def serviceIds(implicit connection: Connection) = pullColumn(model.serviceId.column).map { v => v.getInt }
	
	/**
	  * address ids of the accessible email service users
	  */
	def addressIds(implicit connection: Connection) = pullColumn(model.addressId.column).map { v => v.getInt }
	
	/**
	  * creation times of the accessible email service users
	  */
	def creationTimes(implicit connection: Connection) = 
		pullColumn(model.created.column).map { v => v.getInstant }
	
	/**
	  * Unique ids of the accessible email service users
	  */
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = EmailServiceUserDbModel
}

