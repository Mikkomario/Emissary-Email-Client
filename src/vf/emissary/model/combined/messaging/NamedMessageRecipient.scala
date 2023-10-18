package vf.emissary.model.combined.messaging

import utopia.flow.view.template.Extender
import vf.emissary.model.partial.messaging.AddressData
import vf.emissary.model.stored.messaging.MessageRecipientLink

/**
 * Includes address and name information to a message recipient entry
 * @author Mikko Hilpinen
 * @since 17.10.2023, v0.1
 */
case class NamedMessageRecipient(recipient: NamedAddress, messageLink: MessageRecipientLink)
	extends Extender[AddressData]
{
	// COMPUTED --------------------------
	
	/**
	 * @return Id of this recipient / address
	 */
	def id = recipient.id
	
	/**
	 * @return Id of the message received by this address
	 */
	def messageId = messageLink.messageId
	/**
	 * @return The role of this message recipient
	 */
	def role = messageLink.data.role
	
	
	// IMPLEMENTED  ----------------------
	
	override def wrapped: AddressData = recipient.wrapped
	
	override def toString = recipient.toString
}