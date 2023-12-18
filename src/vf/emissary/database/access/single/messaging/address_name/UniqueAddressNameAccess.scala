package vf.emissary.database.access.single.messaging.address_name

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.AddressNameFactory
import vf.emissary.database.model.messaging.AddressNameModel
import vf.emissary.model.stored.messaging.AddressName

import java.time.Instant

object UniqueAddressNameAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueAddressNameAccess = new _UniqueAddressNameAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueAddressNameAccess(condition: Condition) extends UniqueAddressNameAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct address names.
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
trait UniqueAddressNameAccess 
	extends SingleRowModelAccess[AddressName] with FilterableView[UniqueAddressNameAccess] 
		with DistinctModelAccess[AddressName, Option[AddressName], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the address to which this name corresponds. None if no address name (or value) was found.
	  */
	def addressId(implicit connection: Connection) = pullColumn(model.addressIdColumn).int
	
	/**
	  * Human-readable name of this entity, if available. None if no address name (or value) was found.
	  */
	def name(implicit connection: Connection) = pullColumn(model.nameColumn).getString
	
	/**
	  * Time when this link was first documented. None if no address name (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.createdColumn).instant
	
	/**
	  * Whether this name is used by this person themselves. None if no address name (or value) was found.
	  */
	def isSelfAssigned(implicit connection: Connection) = pullColumn(model.isSelfAssignedColumn).boolean
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = AddressNameModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = AddressNameFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueAddressNameAccess = 
		new UniqueAddressNameAccess._UniqueAddressNameAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the address ids of the targeted address names
	  * @param newAddressId A new address id to assign
	  * @return Whether any address name was affected
	  */
	def addressId_=(newAddressId: Int)(implicit connection: Connection) = 
		putColumn(model.addressIdColumn, newAddressId)
	
	/**
	  * Updates the creation times of the targeted address names
	  * @param newCreated A new created to assign
	  * @return Whether any address name was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the are self assigned of the targeted address names
	  * @param newIsSelfAssigned A new is self assigned to assign
	  * @return Whether any address name was affected
	  */
	def isSelfAssigned_=(newIsSelfAssigned: Boolean)(implicit connection: Connection) = 
		putColumn(model.isSelfAssignedColumn, newIsSelfAssigned)
	
	/**
	  * Updates the names of the targeted address names
	  * @param newName A new name to assign
	  * @return Whether any address name was affected
	  */
	def name_=(newName: String)(implicit connection: Connection) = putColumn(model.nameColumn, newName)
}

