package vf.emissary.database.access.single.messaging.address

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import vf.emissary.database.model.messaging.AddressModel

import java.time.Instant

/**
  * A common trait for access points which target individual addresses or similar items at a time
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
trait UniqueAddressAccessLike[+A] 
	extends SingleModelAccess[A] with DistinctModelAccess[A, Option[A], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * The address of this address. None if no address (or value) was found.
	  */
	def address(implicit connection: Connection) = pullColumn(model.addressColumn).getString
	
	/**
	  * Time when this address was added to the database. None if no address (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.createdColumn).instant
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = AddressModel
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the addresses of the targeted addresses
	  * @param newAddress A new address to assign
	  * @return Whether any address was affected
	  */
	def address_=(newAddress: String)(implicit connection: Connection) = 
		putColumn(model.addressColumn, newAddress)
	
	/**
	  * Updates the creation times of the targeted addresses
	  * @param newCreated A new created to assign
	  * @return Whether any address was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
}

