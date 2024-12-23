package vf.emissary.database.access.single.messaging.address

import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{SubView, UnconditionalView, View}
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.AddressDbFactory
import vf.emissary.database.storable.messaging.AddressDbModel
import vf.emissary.model.stored.messaging.Address

/**
  * Used for accessing individual addresses
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbAddress extends SingleRowModelAccess[Address] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	private def model = AddressDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = AddressDbFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted address
	  * @return An access point to that address
	  */
	def apply(id: Int) = DbSingleAddress(id)
	/**
	 * @param address An email address as a string
	 * @return Access to that address' information
	 */
	def apply(address: String) = new DbSpecificAddress(address)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  * unique addresses.
	  * @return An access point to the address that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = UniqueAddressAccess(mergeCondition(condition))
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield unique addresses.
	  * @return An access point to the address that satisfies the specified condition
	  */
	private def distinct(condition: Condition) = UniqueAddressAccess(condition)
	
	
	// NESTED	--------------------
	
	class DbSpecificAddress(address: String) extends UniqueAddressAccess with SubView
	{
		// ATTRIBUTES	--------------------
		
		private lazy val dataModel = model.withAddress(address)
		
		override lazy val filterCondition: Condition = dataModel.toCondition
		
		
		// IMPLEMENTED	--------------------
		
		override protected def parent: View = DbAddress
		
		
		// OTHER	--------------------
		
		/**
		  * Retrieves the id of this address from the DB. Inserts this address, if not found from the DB.
		  * @param connection Implicit DB connection
		  * @return Either Left: Newly inserted address id, or Right: existing address id
		  */
		def pullOrInsertId()(implicit connection: Connection) = id.toRight { dataModel.insert().getInt }
	}
}

