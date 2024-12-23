package vf.emissary.database.access.single.messaging.service.user

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.EmailServiceUserDbFactory
import vf.emissary.database.storable.messaging.EmailServiceUserDbModel
import vf.emissary.model.partial.messaging.EmailServiceUserData
import vf.emissary.model.stored.messaging.EmailServiceUser

/**
  * Used for accessing individual email service users
  * @author Mikko Hilpinen
  * @since 22.12.2024, v1.1
  */
object DbEmailServiceUser extends SingleRowModelAccess[EmailServiceUser] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	private def model = EmailServiceUserDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = EmailServiceUserDbFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted email service user
	  * @return An access point to that email service user
	  */
	def apply(id: Int) = DbSingleEmailServiceUser(id)
	
	/**
	 * @param serviceId Id of the targeted email service
	 * @param addressId Id of the targeted email address
	 * @return Access to that user
	 */
	def ofServiceWithAddress(serviceId: Int, addressId: Int) = new DbSpecificUser(serviceId, addressId)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield unique email service users.
	  * @return An access point to the email service user that satisfies the specified condition
	  */
	private def distinct(condition: Condition) = UniqueEmailServiceUserAccess(condition)
	
	
	// NESTED   --------------------
	
	class DbSpecificUser(serviceId: Int, addressId: Int) extends UniqueEmailServiceUserAccess
	{
		// ATTRIBUTES   ------------
		
		override lazy val accessCondition: Option[Condition] =
			Some(model.serviceId <=> serviceId && model.addressId <=> addressId)
			
		
		// OTHER    ----------------
		
		/**
		 * If this entry is not yet found from the DB, inserts it
		 * @param connection Implicit DB connection
		 * @return Inserted user data. None if already found from the DB.
		 */
		def insertIfMissing()(implicit connection: Connection) =
			if (nonEmpty) None else Some(insert())
		
		/**
		 * Retrieves this user's information. Inserts an entry if one was not found.
		 * @param connection Implicit DB connection
		 * @return Pulled (right) or inserted (left) user
		 */
		def pullOrInsert()(implicit connection: Connection) =
			pull.toRight { insert() }
			
		private def insert()(implicit connection: Connection) =
			model.insert(EmailServiceUserData(serviceId, addressId))
	}
}

