package vf.emissary.database.access.many.messaging.address_name

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.AddressNameFactory
import vf.emissary.database.model.messaging.AddressNameModel
import vf.emissary.model.stored.messaging.AddressName

import java.time.Instant

object ManyAddressNamesAccess
{
	// NESTED	--------------------
	
	private class ManyAddressNamesSubView(condition: Condition) extends ManyAddressNamesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple address names at a time
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
trait ManyAddressNamesAccess 
	extends ManyRowModelAccess[AddressName] with FilterableView[ManyAddressNamesAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * address ids of the accessible address names
	  */
	def addressIds(implicit connection: Connection) = pullColumn(model.addressIdColumn).map { v => v.getInt }
	
	/**
	  * names of the accessible address names
	  */
	def names(implicit connection: Connection) = pullColumn(model.nameColumn).flatMap { _.string }
	
	/**
	  * creation times of the accessible address names
	  */
	def creationTimes(implicit connection: Connection) = pullColumn(model.createdColumn)
		.map { v => v.getInstant }
	
	/**
	  * are self assigned of the accessible address names
	  */
	def areSelfAssigned(implicit connection: Connection) = 
		pullColumn(model.isSelfAssignedColumn).map { v => v.getBoolean }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = AddressNameModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = AddressNameFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyAddressNamesAccess = 
		new ManyAddressNamesAccess.ManyAddressNamesSubView(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	 * @param names Targeted names / strings
	 * @return Access to name-links where the names contain any of the specified strings
	 */
	def like(names: Seq[String]) = filter(Condition.or(names.map(model.nameColumn.contains)))
	
	/**
	  * Updates the address ids of the targeted address names
	  * @param newAddressId A new address id to assign
	  * @return Whether any address name was affected
	  */
	def addressIds_=(newAddressId: Int)(implicit connection: Connection) = 
		putColumn(model.addressIdColumn, newAddressId)
	
	/**
	  * Updates the are self assigned of the targeted address names
	  * @param newIsSelfAssigned A new is self assigned to assign
	  * @return Whether any address name was affected
	  */
	def areSelfAssigned_=(newIsSelfAssigned: Boolean)(implicit connection: Connection) = 
		putColumn(model.isSelfAssignedColumn, newIsSelfAssigned)
	
	/**
	  * Updates the creation times of the targeted address names
	  * @param newCreated A new created to assign
	  * @return Whether any address name was affected
	  */
	def creationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the names of the targeted address names
	  * @param newName A new name to assign
	  * @return Whether any address name was affected
	  */
	def names_=(newName: String)(implicit connection: Connection) = putColumn(model.nameColumn, newName)
}

