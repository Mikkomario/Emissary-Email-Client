package vf.emissary.database.access.many.messaging.address

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.AddressFactory
import vf.emissary.database.model.messaging.AddressModel
import vf.emissary.model.stored.messaging.Address

import java.time.Instant

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
	extends ManyRowModelAccess[Address] with FilterableView[ManyAddressesAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * addresses of the accessible addresses
	  */
	def addresses(implicit connection: Connection) = pullColumn(model.addressColumn).flatMap { _.string }
	
	/**
	  * names of the accessible addresses
	  */
	def names(implicit connection: Connection) = pullColumn(model.nameColumn).flatMap { _.string }
	
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
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = AddressFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyAddressesAccess = 
		new ManyAddressesAccess.ManyAddressesSubView(mergeCondition(filterCondition))
	
	
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
	
	/**
	  * Updates the names of the targeted addresses
	  * @param newName A new name to assign
	  * @return Whether any address was affected
	  */
	def names_=(newName: String)(implicit connection: Connection) = putColumn(model.nameColumn, newName)
}

