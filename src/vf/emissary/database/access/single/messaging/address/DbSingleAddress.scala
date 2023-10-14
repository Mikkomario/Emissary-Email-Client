package vf.emissary.database.access.single.messaging.address

import utopia.flow.collection.CollectionExtensions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.database.access.single.messaging.address_name.DbAddressName
import vf.emissary.database.model.messaging.AddressNameModel
import vf.emissary.model.partial.messaging.AddressNameData
import vf.emissary.model.stored.messaging.Address

/**
  * An access point to individual addresses, based on their id
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class DbSingleAddress(id: Int) extends UniqueAddressAccess with SingleIntIdModelAccess[Address]
{
	/**
	 * @param name Targeted name
	 * @return Access to that name's DB entry
	 */
	def accessName(name: String) = DbAddressName(id, name)
	
	/**
	 * @param name Targeted name
	 * @param connection Implicit DB connection
	 * @return Whether this address has the specified name assigned to it
	 */
	def hasName(name: String)(implicit connection: Connection) = accessName(name).nonEmpty
	
	/**
	 * Assigns a new name for this address. Avoids inserting duplicate information.
	 * @param name Name to assign
	 * @param selfAssigned Whether this name should be considered self-assigned
	 * @param connection Implicit DB connection
	 */
	def assignName(name: String, selfAssigned: Boolean = false)(implicit connection: Connection): Unit = {
		// Case: Specifying a self-assigned name
		// => Makes sure the existing linked name (if applicable) is marked as self-assigned
		if (selfAssigned)
			accessName(name).pullOrInsert(insertAsSelfAssigned = selfAssigned).leftOrMap { entry =>
				if (entry.isSelfAssigned != selfAssigned)
					entry.access.isSelfAssigned = selfAssigned
			}
		// Case: Specifying a normal name => Assigns a new name, if appropriate
		else if (!hasName(name))
			AddressNameModel.insert(AddressNameData(id, name, isSelfAssigned = selfAssigned))
	}
}
