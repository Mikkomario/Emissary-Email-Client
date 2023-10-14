package vf.emissary.database.access.single.messaging.address

import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{SubView, UnconditionalView, View}
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.AddressFactory
import vf.emissary.database.model.messaging.AddressModel
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
	  * Factory used for constructing database the interaction models
	  */
	protected def model = AddressModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = AddressFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted address
	  * @return An access point to that address
	  */
	def apply(id: Int) = DbSingleAddress(id)
	
	/**
	 * @param address Targeted address
	 * @return Access to that address' data in the DB
	 */
	def apply(address: String) = new DbSpecificAddress(address)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique addresses.
	  * @return An access point to the address that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = UniqueAddressAccess(mergeCondition(condition))
	
	
	// NESTED   -------------------------
	
	class DbSpecificAddress(address: String) extends UniqueAddressAccess with SubView
	{
		// ATTRIBUTES   ----------------
		
		private lazy val dataModel = model.withAddress(address)
		override lazy val filterCondition: Condition = dataModel.toCondition
		
		
		// IMPLEMENTED  ----------------
		
		override protected def parent: View = DbAddress
		
		
		// OTHER    --------------------
		
		/**
		 * Retrieves the id of this address from the DB. Inserts this address, if not found from the DB.
		 * @param connection Implicit DB connection
		 * @return Either Left: Newly inserted address id, or Right: existing address id
		 */
		def pullOrInsertId()(implicit connection: Connection) =
			id.toRight { dataModel.insert().getInt }
	}
}

