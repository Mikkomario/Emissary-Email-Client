package vf.emissary.database.access.single.messaging.service

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.EmailServiceDbFactory
import vf.emissary.database.storable.messaging.EmailServiceDbModel
import vf.emissary.model.stored.messaging.EmailService

/**
  * Used for accessing individual email services
  * @author Mikko Hilpinen
  * @since 22.12.2024, v1.1
  */
object DbEmailService extends SingleRowModelAccess[EmailService] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	private def model = EmailServiceDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = EmailServiceDbFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted email service
	  * @return An access point to that email service
	  */
	def apply(id: Int) = DbSingleEmailService(id)
	
	/**
	 * @param address Targeted email server address
	 * @return Access to that server's information
	 */
	def apply(address: String) = distinct(model.address <=> address)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield unique email services.
	  * @return An access point to the email service that satisfies the specified condition
	  */
	private def distinct(condition: Condition) = UniqueEmailServiceAccess(condition)
}

