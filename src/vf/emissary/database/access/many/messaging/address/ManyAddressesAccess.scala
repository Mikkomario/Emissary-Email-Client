package vf.emissary.database.access.many.messaging.address

import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.sql.Condition
import vf.emissary.database.access.many.messaging.address_name.DbAddressNames
import vf.emissary.database.factory.messaging.AddressFactory
import vf.emissary.model.combined.messaging.NamedAddress
import vf.emissary.model.stored.messaging.Address

object ManyAddressesAccess
{
	// NESTED	--------------------
	
	private class ManyAddressesSubView(condition: Condition) extends ManyAddressesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple addresses at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait ManyAddressesAccess 
	extends ManyAddressesAccessLike[Address, ManyAddressesAccess] with ManyRowModelAccess[Address]
{
	// COMPUTED ------------------------
	
	/**
	 * @return Copy of this access point, including associated name entries
	 */
	def withNames = DbNamedAddresses.filter(globalCondition)
	
	/**
	 * @param connection Implicit DB connection
	 * @return All accessible addresses, including their name entries
	 */
	def pullWithNames(implicit connection: Connection) = {
		val addresses = pull
		if (addresses.nonEmpty) {
			// Pulls associated name-entries
			val namesPerAddressId = DbAddressNames.forAddresses(addresses.map { _.id }).pull.groupBy { _.addressId }
			// Combines the information together
			addresses.map { a => NamedAddress(a, namesPerAddressId.getOrElse(a.id, Vector.empty)) }
		}
		else
			Vector()
	}
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = AddressFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyAddressesAccess = 
		new ManyAddressesAccess.ManyAddressesSubView(mergeCondition(filterCondition))
}

