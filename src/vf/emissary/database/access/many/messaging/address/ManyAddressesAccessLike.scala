package vf.emissary.database.access.many.messaging.address

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.emissary.database.storable.messaging.AddressDbModel

/**
  * A common trait for access points which target multiple addresses or similar instances at a time
  * @tparam A Type of read (addresses -like) instances
  * @tparam Repr Type of this access point
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
trait ManyAddressesAccessLike[+A, +Repr] extends ManyModelAccess[A] with Indexed with FilterableView[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * addresses of the accessible addresses
	  */
	def addresses(implicit connection: Connection) = pullColumn(model.address.column).flatMap { _.string }
	/**
	  * creation times of the accessible addresses
	  */
	def creationTimes(implicit connection: Connection) = 
		pullColumn(model.created.column).map { v => v.getInstant }
	/**
	  * Unique ids of the accessible addresses
	  */
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = AddressDbModel
	
	
	// OTHER	--------------------
	
	/**
	 * @param address address to target
	 * @return Copy of this access point that only includes addresses with the specified address
	 */
	def matching(address: String) = filter(model.address.column <=> address)
	/**
	 * @param addresses Targeted addresses
	 * @return Copy of this access point that only includes addresses where address is within the specified value set
	 */
	def matchingAnyOf(addresses: Iterable[String]) = filter(model.address.column.in(addresses))
	
	/**
	  * @param address Partial email address
	  * @return Access to addresses that contain the specified string
	  */
	def like(address: String) = filter(model.address.column.contains(address))
	/**
	  * @param addresses Targeted addresses / strings
	  * @return Access to addresses where any of the specified strings are mentioned
	  */
	def like(addresses: Seq[String]) = filter(Condition.or(addresses.map(model.address.column.contains)))
}

