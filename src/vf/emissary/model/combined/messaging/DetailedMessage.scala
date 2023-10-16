package vf.emissary.model.combined.messaging

import utopia.flow.view.template.Extender
import vf.emissary.model.partial.messaging.MessageData
import vf.emissary.model.stored.messaging.{Attachment, Message}

/**
 * Combines all information concerning an individual message
 * @author Mikko Hilpinen
 * @since 15.10.2023, v0.1
 */
case class DetailedMessage(message: Message, sender: NamedAddress, statements: Vector[DetailedStatement],
                           attachments: Vector[Attachment])
	extends Extender[MessageData]
{
	// COMPUTED -----------------------
	
	/**
	 * @return Id of this message
	 */
	def id = message.id
	
	
	// IMPLEMENTED  -------------------
	
	override def wrapped: MessageData = message.data
	
	override def toString = s"$sender:\n${statements.mkString}"
}