package vf.emissary.model.combined.messaging

import utopia.flow.view.template.Extender
import utopia.vault.model.template.HasId
import vf.emissary.model.factory.messaging.EmailServiceUserFactoryWrapper
import vf.emissary.model.partial.messaging.EmailServiceUserData
import vf.emissary.model.stored.messaging.{EmailService, EmailServiceUser}

/**
 * Includes email address and email service information with a user entry
 *
 * @author Mikko Hilpinen
 * @since 22.12.2024, v1.1
 */
case class DetailedEmailServiceUser(user: EmailServiceUser, service: EmailService, address: NamedAddress)
	extends Extender[EmailServiceUserData]
		with EmailServiceUserFactoryWrapper[EmailServiceUser, DetailedEmailServiceUser] with HasId[Int]
{
	// ATTRIBUTES   ----------------------
	
	/**
	 * Primary name assignment of for this user.
	 * None if no name exists.
	 */
	lazy val name = address.name
	
	
	// COMPUTED --------------------------
	
	/**
	 * @return This user's email address
	 */
	def emailAddress = address.address.address
	
	
	// IMPLEMENTED  ----------------------
	
	override def wrapped: EmailServiceUserData = user.data
	override protected def wrappedFactory: EmailServiceUser = user
	
	override def id: Int = user.id
	
	override def toString = address.toString
	
	override protected def wrap(factory: EmailServiceUser): DetailedEmailServiceUser = copy(user = factory)
}
