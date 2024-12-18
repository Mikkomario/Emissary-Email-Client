package vf.emissary.database.access.many.messaging.address

import utopia.vault.database.Connection
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.NamedAddressDbFactory
import vf.emissary.database.storable.messaging.AddressNameDbModel
import vf.emissary.model.combined.messaging.NamedAddress

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
  * A common trait for access points that return multiple named addresses at a time
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
		pullColumn(nameModel.addressId.column).map { v => v.getInt }
	/**
	  * names of the accessible address names
	  */
	def nameNames(implicit connection: Connection) = pullColumn(nameModel.name.column).flatMap { _.string }
	/**
	  * creation times of the accessible address names
	  */
	def nameCreationTimes(implicit connection: Connection) = 
		pullColumn(nameModel.created.column).map { v => v.getInstant }
	/**
	  * are self assigned of the accessible address names
	  */
	def nameAreSelfAssigned(implicit connection: Connection) = 
		pullColumn(nameModel.isSelfAssigned.column).map { v => v.getBoolean }
	
	/**
	  * Model (factory) used for interacting the address names associated with this named address
	  */
	protected def nameModel = AddressNameDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = NamedAddressDbFactory
	override protected def self = this
	
	override def apply(condition: Condition): ManyNamedAddressesAccess = ManyNamedAddressesAccess(condition)
	
	
	// OTHER	--------------------
	
	/**
	  * @param namePart Searched name part
	  * @return Access to addresses that are associated with a name that contains the specified string
	  */
	def withNameLike(namePart: String) = filter(nameModel.name.column.contains(namePart))
	/**
	  * @param string Searched name or address part
	  * @return Access to addresses that are associated with a similar name or contain the specified
	  * string in their address
	  */
	def withNameOrAddressLike(string: String) = 
		filter(nameModel.name.column.contains(string) || model.address.column.contains(string))
}

