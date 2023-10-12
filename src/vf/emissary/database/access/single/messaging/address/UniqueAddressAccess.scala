package vf.emissary.database.access.single.messaging.address

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.AddressFactory
import vf.emissary.database.model.messaging.AddressModel
import vf.emissary.model.stored.messaging.Address

import java.time.Instant

object UniqueAddressAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueAddressAccess = new _UniqueAddressAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueAddressAccess(condition: Condition) extends UniqueAddressAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct addresses.
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait UniqueAddressAccess 
	extends SingleRowModelAccess[Address] with FilterableView[UniqueAddressAccess] 
		with DistinctModelAccess[Address, Option[Address], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * The address of this address. None if no address (or value) was found.
	  */
	def address(implicit connection: Connection) = pullColumn(model.addressColumn).getString
	
	/**
	  * Human-readable name of this entity, if available. None if no address (or value) was found.
	  */
	def name(implicit connection: Connection) = pullColumn(model.nameColumn).getString
	
	/**
	  * Time when this address was added to the database. None if no address (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.createdColumn).instant
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = AddressModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = AddressFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueAddressAccess = 
		new UniqueAddressAccess._UniqueAddressAccess(mergeCondition(filterCondition))
	
	
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
	
	/**
	  * Updates the names of the targeted addresses
	  * @param newName A new name to assign
	  * @return Whether any address was affected
	  */
	def name_=(newName: String)(implicit connection: Connection) = putColumn(model.nameColumn, newName)
}

