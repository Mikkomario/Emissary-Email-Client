package vf.emissary.database.access.many.messaging.address

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.NamedAddressFactory
import vf.emissary.database.model.messaging.AddressNameModel
import vf.emissary.model.combined.messaging.NamedAddress

import java.time.Instant

object ManyNamedAddressesAccess extends ViewFactory[ManyNamedAddressesAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyNamedAddressesAccess = 
		_ManyNamedAddressesAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyNamedAddressesAccess(override val accessCondition: Option[Condition]) 
		extends ManyNamedAddressesAccess
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
	
	override def apply(condition: Condition): ManyNamedAddressesAccess = ManyNamedAddressesAccess(condition)
	
	
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
	
	/**
	  * @param namePart Searched name part
	  * @return Access to addresses that are associated with a name that contains the specified string
	  */
	def withNameLike(namePart: String) = filter(nameModel.nameColumn.contains(namePart))
	
	/**
	  * @param string Searched name or address part
	  * @return Access to addresses that are associated with a similar name or contain the specified
	  * string in their address
	  */
	def withNameOrAddressLike(string: String) = 
		filter(nameModel.nameColumn.contains(string) || model.addressColumn.contains(string))
}

