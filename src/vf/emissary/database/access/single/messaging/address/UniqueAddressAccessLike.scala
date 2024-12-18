package vf.emissary.database.access.single.messaging.address

import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import vf.emissary.database.storable.messaging.AddressDbModel

/**
  * A common trait for access points which target individual addresses or similar items at a time
  * @tparam A Type of read (addresses -like) instances
  * @tparam Repr Type of this access point
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
trait UniqueAddressAccessLike[+A, +Repr] 
	extends SingleModelAccess[A] with DistinctModelAccess[A, Option[A], Value] with FilterableView[Repr] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * A string representation of this address. 
	  * None if no address (or value) was found.
	  */
	def address(implicit connection: Connection) = pullColumn(model.address.column).getString
	/**
	  * Time when this address was added to the database. 
	  * None if no address (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.created.column).instant
	/**
	  * Unique id of the accessible address. None if no address was accessible.
	  */
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = AddressDbModel
}

