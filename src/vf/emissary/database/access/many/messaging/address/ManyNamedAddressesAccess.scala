package vf.emissary.database.access.many.messaging.address

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.NamedAddressFactory
import vf.emissary.database.model.messaging.AddressNameModel
import vf.emissary.model.combined.messaging.NamedAddress

import java.time.Instant

object ManyNamedAddressesAccess
{
	// NESTED	--------------------
	
	private class SubAccess(condition: Condition) extends ManyNamedAddressesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return multiple named addressses at a time
  * @author Mikko Hilpinen
  * @since 13.10.2023
  */
trait ManyNamedAddressesAccess extends ManyAddressesAccessLike[NamedAddress, ManyNamedAddressesAccess]
{
	// COMPUTED	--------------------
	
	/**
	  * address ids of the accessible address names
	  */
	def nameAddressIds(implicit connection: Connection) =
		pullColumn(nameModel.addressIdColumn).map { v => v.getInt }
	
	/**
	  * names of the accessible address names
	  */
	def nameNames(implicit connection: Connection) = pullColumn(nameModel.nameColumn).flatMap { _.string }
	
	/**
	  * creation times of the accessible address names
	  */
	def nameCreationTimes(implicit connection: Connection) =
		pullColumn(nameModel.createdColumn).map { v => v.getInstant }
	
	/**
	  * are self assigned of the accessible address names
	  */
	def nameAreSelfAssigned(implicit connection: Connection) =
		pullColumn(nameModel.isSelfAssignedColumn).map { v => v.getBoolean }
	
	/**
	  * Model (factory) used for interacting the address names associated with this named address
	  */
	protected def nameModel = AddressNameModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = NamedAddressFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyNamedAddressesAccess =
		new ManyNamedAddressesAccess.SubAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the address ids of the targeted address names
	  * @param newAddressId A new address id to assign
	  * @return Whether any address name was affected
	  */
	def nameAddressIds_=(newAddressId: Int)(implicit connection: Connection) = 
		putColumn(nameModel.addressIdColumn, newAddressId)
	
	/**
	  * Updates the are self assigned of the targeted address names
	  * @param newIsSelfAssigned A new is self assigned to assign
	  * @return Whether any address name was affected
	  */
	def nameAreSelfAssigned_=(newIsSelfAssigned: Boolean)(implicit connection: Connection) = 
		putColumn(nameModel.isSelfAssignedColumn, newIsSelfAssigned)
	
	/**
	  * Updates the creation times of the targeted address names
	  * @param newCreated A new created to assign
	  * @return Whether any address name was affected
	  */
	def nameCreationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(nameModel.createdColumn, newCreated)
	
	/**
	  * Updates the names of the targeted address names
	  * @param newName A new name to assign
	  * @return Whether any address name was affected
	  */
	def nameNames_=(newName: String)(implicit connection: Connection) = putColumn(nameModel.nameColumn, 
		newName)
}

