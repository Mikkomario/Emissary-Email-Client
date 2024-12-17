package vf.emissary.database.access.many.messaging.address

import utopia.flow.collection.immutable.Empty
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.emissary.database.access.many.messaging.address_name.DbAddressNames
import vf.emissary.database.factory.messaging.AddressFactory
import vf.emissary.model.combined.messaging.NamedAddress
import vf.emissary.model.stored.messaging.Address

object ManyAddressesAccess extends ViewFactory[ManyAddressesAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyAddressesAccess = _ManyAddressesAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyAddressesAccess(override val accessCondition: Option[Condition]) 
		extends ManyAddressesAccess
}

/**
  * A common trait for access points which target multiple addresses at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait ManyAddressesAccess 
	extends ManyAddressesAccessLike[Address, ManyAddressesAccess] with ManyRowModelAccess[Address]
{
	// COMPUTED	--------------------
	
	/**
	  * Copy of this access point, including associated name entries
	  */
	def withNames = DbNamedAddresses.filter(accessCondition)
	
	/**
	  * All accessible addresses, including their name entries
	  * @param connection Implicit DB connection
	  */
	def pullWithNames(implicit connection: Connection) = {
		val addresses = pull
		if (addresses.nonEmpty) {
			// Pulls associated name-entries
			val namesPerAddressId = DbAddressNames.forAddresses(addresses.map { _.id }).pull.groupBy { _.addressId }
			// Combines the information together
			addresses.map { a => NamedAddress(a, namesPerAddressId.getOrElse(a.id, Empty)) }
		}
		else
			Empty
	}
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = AddressFactory
	override protected def self = this
	
	override def apply(condition: Condition): ManyAddressesAccess = ManyAddressesAccess(condition)
}

