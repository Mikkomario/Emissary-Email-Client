package vf.emissary.database.access.many.messaging.address

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import vf.emissary.database.model.messaging.AddressModel

import java.time.Instant

/**
  * A common trait for access points which target multiple addresses or similar instances at a time
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
trait ManyAddressesAccessLike[+A, +Repr] extends ManyModelAccess[A] with Indexed with FilterableView[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * addresses of the accessible addresses
	  */
	def addresses(implicit connection: Connection) = pullColumn(model.addressColumn).flatMap { _.string }
	
	/**
	  * creation times of the accessible addresses
	  */
	def creationTimes(implicit connection: Connection) = pullColumn(model.createdColumn)
		.map { v => v.getInstant }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
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
	def addresses_=(newAddress: String)(implicit connection: Connection) = 
		putColumn(model.addressColumn, newAddress)
	
	/**
	  * Updates the creation times of the targeted addresses
	  * @param newCreated A new created to assign
	  * @return Whether any address was affected
	  */
	def creationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
}

