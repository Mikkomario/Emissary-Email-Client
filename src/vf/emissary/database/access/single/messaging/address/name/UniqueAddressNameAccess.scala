package vf.emissary.database.access.single.messaging.address.name

import utopia.flow.generic.model.immutable.Value
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{FilterableView, ViewFactory}
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.AddressNameDbFactory
import vf.emissary.database.storable.messaging.AddressNameDbModel
import vf.emissary.model.stored.messaging.AddressName

object UniqueAddressNameAccess extends ViewFactory[UniqueAddressNameAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override
		 def apply(condition: Condition): UniqueAddressNameAccess = _UniqueAddressNameAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _UniqueAddressNameAccess(override val accessCondition: Option[Condition]) 
		extends UniqueAddressNameAccess
}

/**
  * A common trait for access points that return individual and distinct address names.
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait UniqueAddressNameAccess 
	extends SingleRowModelAccess[AddressName] 
		with DistinctModelAccess[AddressName, Option[AddressName], Value] 
		with FilterableView[UniqueAddressNameAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the address to which this name corresponds. 
	  * None if no address name (or value) was found.
	  */
	def addressId(implicit connection: Connection) = pullColumn(model.addressId.column).int
	/**
	  * Human-readable name of this entity, if available. 
	  * None if no address name (or value) was found.
	  */
	def name(implicit connection: Connection) = pullColumn(model.name.column).getString
	/**
	  * Time when this link was first documented. 
	  * None if no address name (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.created.column).instant
	/**
	  * Whether this name is used by this person themselves. 
	  * None if no address name (or value) was found.
	  */
	def isSelfAssigned(implicit connection: Connection) = pullColumn(model.isSelfAssigned.column).boolean
	/**
	  * Unique id of the accessible address name. None if no address name was accessible.
	  */
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = AddressNameDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = AddressNameDbFactory
	override def self = this
	
	override def apply(condition: Condition): UniqueAddressNameAccess = UniqueAddressNameAccess(condition)
	
	
	// OTHER    ------------------------
	
	def isSelfAssigned_=(selfAssigned: Boolean)(implicit connection: Connection) =
		putColumn(model.isSelfAssigned.column, selfAssigned)
}

