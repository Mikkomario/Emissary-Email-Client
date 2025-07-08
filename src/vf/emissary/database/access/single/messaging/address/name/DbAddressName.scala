package vf.emissary.database.access.single.messaging.address.name

import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{SubView, UnconditionalView, View}
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.AddressNameDbFactory
import vf.emissary.database.storable.messaging.AddressNameDbModel
import vf.emissary.model.partial.messaging.AddressNameData
import vf.emissary.model.stored.messaging.AddressName

/**
  * Used for accessing individual address names
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object DbAddressName extends SingleRowModelAccess[AddressName] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	private def model = AddressNameDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = AddressNameDbFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted address name
	  * @return An access point to that address name
	  */
	def apply(id: Int) = DbSingleAddressName(id)
	
	/**
	 * @param addressId Id of the associated address
	 * @param name Name assigned to that address
	 * @return Access to that address name link
	 */
	def apply(addressId: Int, name: String) = new DbSpecificAddressName(addressId, name)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield unique address names.
	  * @return An access point to the address name that satisfies the specified condition
	  */
	private def distinct(condition: Condition) = UniqueAddressNameAccess(condition)
	
	
	// NESTED   -----------------------
	
	class DbSpecificAddressName(addressId: Int, name: String) extends UniqueAddressNameAccess with SubView
	{
		// IMPLEMENTED  -------------
		
		override protected def parent: View = DbAddressName
		override def filterCondition: Condition = this.model.withAddressId(addressId).withName(name).toCondition
		
		
		// OTHER    ----------------
		
		/**
		 * Retrieves this link from the database. Inserts if not found.
		 * @param insertAsSelfAssigned Whether this name should be considered self-assigned if inserted
		 * @param connection Implicit DB Connection
		 * @return Pulled (right) or inserted (left) address name entry
		 */
		def pullOrInsert(insertAsSelfAssigned: => Boolean = false)(implicit connection: Connection) =
			pull.toRight { this.model.insert(AddressNameData(addressId, name, isSelfAssigned = insertAsSelfAssigned)) }
	}
}

