package vf.emissary.database.access.many.messaging.address.name

import utopia.flow.collection.immutable.IntSet
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{FilterableView, ViewFactory}
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.AddressNameDbFactory
import vf.emissary.database.storable.messaging.AddressNameDbModel
import vf.emissary.model.stored.messaging.AddressName

object ManyAddressNamesAccess extends ViewFactory[ManyAddressNamesAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override
		 def apply(condition: Condition): ManyAddressNamesAccess = _ManyAddressNamesAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyAddressNamesAccess(override val accessCondition: Option[Condition]) 
		extends ManyAddressNamesAccess
}

/**
  * A common trait for access points which target multiple address names at a time
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait ManyAddressNamesAccess 
	extends ManyRowModelAccess[AddressName] with FilterableView[ManyAddressNamesAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * address ids of the accessible address names
	  */
	def addressIds(implicit connection: Connection) = pullColumn(model.addressId.column).map { v => v.getInt }
	
	/**
	  * names of the accessible address names
	  */
	def names(implicit connection: Connection) = pullColumn(model.name.column).flatMap { _.string }
	
	/**
	  * creation times of the accessible address names
	  */
	def creationTimes(implicit connection: Connection) = 
		pullColumn(model.created.column).map { v => v.getInstant }
	
	/**
	  * are self assigned of the accessible address names
	  */
	def areSelfAssigned(implicit connection: Connection) = 
		pullColumn(model.isSelfAssigned.column).map { v => v.getBoolean }
	
	/**
	  * Unique ids of the accessible address names
	  */
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = AddressNameDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = AddressNameDbFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): ManyAddressNamesAccess = ManyAddressNamesAccess(condition)
	
	
	// OTHER	--------------------
	
	/**
	  * @param name name to target
	  * @return Copy of this access point that only includes address names with the specified name
	  */
	def matching(name: String) = filter(model.name.column <=> name)
	
	/**
	  * @param names Targeted names
	  * @return
	  * 
		 Copy of this access point that only includes address names where name is within the specified value set
	  */
	def matchingAnyOf(names: Iterable[String]) = filter(model.name.column.in(names))
	
	/**
	  * @param addressId address id to target
	  * @return Copy of this access point that only includes address names with the specified address id
	  */
	def ofAddress(addressId: Int) = filter(model.addressId.column <=> addressId)
	
	/**
	  * @param addressIds Targeted address ids
	  * @return
	  * 
		 Copy of this access point that only includes address names where address id is within the specified value set
	  */
	def ofAddresses(addressIds: IterableOnce[Int]) = filter(model
		.addressId.column.in(IntSet.from(addressIds)))
}

