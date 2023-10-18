package vf.emissary.model.combined.messaging

import utopia.flow.util.NotEmpty
import utopia.flow.view.template.Extender
import vf.emissary.model.enumeration.RecipientType
import vf.emissary.model.enumeration.RecipientType.{Copy, HiddenCopy, Primary}
import vf.emissary.model.partial.messaging.MessageData
import vf.emissary.model.stored.messaging.{Attachment, Message}

/**
 * Combines all information concerning an individual message
 * @author Mikko Hilpinen
 * @since 15.10.2023, v0.1
 */
case class DetailedMessage(message: Message, sender: NamedAddress, recipients: Vector[NamedMessageRecipient],
                           statements: Vector[DetailedStatement], attachments: Vector[Attachment])
	extends Extender[MessageData]
{
	// ATTRIBUTES   -------------------
	
	/**
	 * A map that contains recipients for each recipient role.
	 * Returns an empty vector as a default value.
	 */
	lazy val recipientsPerType = recipients.groupBy { _.role }.withDefaultValue(Vector.empty)
	
	
	// COMPUTED -----------------------
	
	/**
	 * @return Id of this message
	 */
	def id = message.id
	
	/**
	 * @return Primary message recipients
	 */
	def primaryRecipients = recipientsOfType(Primary)
	
	/**
	 * @return All addresses involved in this message (i.e. sender & recipients)
	 */
	def involvedAddresses = sender +: recipients.map { _.recipient }
	
	
	// IMPLEMENTED  -------------------
	
	override def wrapped: MessageData = message.data
	
	override def toString = {
		val otherRecipientsStr = NotEmpty(recipientsOfType(Copy) ++ recipientsOfType(HiddenCopy)) match {
			case Some(recipients) => s", cc: ${recipients.mkString(", ")}"
			case None => ""
		}
		val attachmentsStr = NotEmpty(attachments) match {
			case Some(attachments) => s"\n(${attachments.size} attachments)"
			case None => ""
		}
		s"From $sender to ${primaryRecipients.mkString(", ")}$otherRecipientsStr:\n${statements.mkString}$attachmentsStr"
	}
	
	
	// OTHER    -----------------------
	
	/**
	 * @param recipientType Targeted recipient type
	 * @return Message recipients of the specified type
	 */
	def recipientsOfType(recipientType: RecipientType) = recipientsPerType(recipientType)
	
	/**
	 * @param addressId Id of the targeted address
	 * @return Whether this message concerns the specified address
	 */
	def involvesAddress(addressId: Int) = sender.id == addressId || recipients.exists { _.id == addressId }
	/**
	 * @param addressId Id of the targeted address
	 * @return Whether the specified address is one of the recipients of this message
	 */
	def involvesAddressAsRecipient(addressId: Int) = recipients.exists { _.id == addressId }
	/**
	 * @param addressId Id of the targeted address
	 * @param recipientCategory Targeted recipient category
	 * @return Whether the specified address is one of the recipients of this message.
	 *         Only considers recipients of the specified type.
	 */
	def involvesAddressAsRecipient(addressId: Int, recipientCategory: RecipientType) =
		recipientsPerType.get(recipientCategory).exists { _.exists { _.id == addressId } }
	
	/**
	 * @param wordId Id of the targeted word
	 * @return Whether this message contains that word
	 */
	def containsWord(wordId: Int) = statements.exists { _.containsWord(wordId) }
}