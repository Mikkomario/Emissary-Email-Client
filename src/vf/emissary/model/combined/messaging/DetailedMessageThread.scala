package vf.emissary.model.combined.messaging

import vf.emissary.model.enumeration.RecipientType
import vf.emissary.model.stored.messaging.MessageThread

import java.time.Instant

object DetailedMessageThread
{
	/**
	 * @param thread Thread to wrap
	 * @param subjects Thread subjects, including text
	 * @param messages Thread messages (full data)
	 * @return A new detailed message thread
	 */
	def apply(thread: MessageThread, subjects: Vector[DetailedSubject],
	          messages: Vector[DetailedMessage]): DetailedMessageThread =
		apply(thread.id, subjects, messages, thread.created)
}

/**
 * Contains all information concerning a single message thread
 * @author Mikko Hilpinen
 * @since 15.10.2023, v0.1
 */
case class DetailedMessageThread(id: Int, subjects: Vector[DetailedSubject], messages: Vector[DetailedMessage],
                                 created: Instant)
{
	// ATTRIBUTES   -------------------
	
	/**
	 * Last used subject on this message thread
	 */
	lazy val subject = subjects.maxByOption { _.created }
	
	/**
	 * Time when the last message in this thread was sent.
	 * None if this thread contains no messages.
	 */
	lazy val lastMessageSendTime = messages.map { _.created }.maxOption
	
	
	// COMPUTED -----------------------
	
	/**
	 * @return All addresses that are involved in this message thread
	 */
	def involvedAddresses = messages.view.flatMap { _.involvedAddresses }.toSet
	
	
	// IMPLEMENTED  -------------------
	
	override def toString = {
		val subjectStr = subject match {
			case Some(subject) => s"$subject:\n\n"
			case None => ""
		}
		s"$subjectStr${messages.mkString("\n\n")}"
	}
	
	
	// OTHER    -----------------------
	
	/**
	 * @param addressId Id of the targeted address
	 * @return Whether that address is involved in this message thread
	 */
	def involvesAddress(addressId: Int) = messages.exists { _.involvesAddress(addressId) }
	/**
	 * @param addressId Id of the targeted address
	 * @return Whether that address has sent at least one message in this thread
	 */
	def involvesAddressAsSender(addressId: Int) = messages.exists { _.senderId == addressId }
	/**
	 * @param addressId Id of the targeted address
	 * @return Whether that address is mentioned at least once as a recipient in this thread
	 */
	def involvesAddressAsRecipient(addressId: Int) = messages.exists { _.involvesAddressAsRecipient(addressId) }
	/**
	 * @param addressId Id of the targeted address
	 * @param recipientCategory Targeted recipient category
	 * @return Whether that address is mentioned at least once in this thread in that recipient category
	 */
	def involvesAddressAsRecipient(addressId: Int, recipientCategory: RecipientType) =
		messages.exists { _.involvesAddressAsRecipient(addressId, recipientCategory) }
	
	/**
	 * @param wordId Id of the targeted word
	 * @return Whether a subject in this thread contains the specified word
	 */
	def containsWordInSubjects(wordId: Int) = subjects.exists { _.containsWord(wordId) }
	/**
	 * @param wordId Id of the targeted word
	 * @return Whether at least one message in this thread contains the specified word
	 */
	def containsWordInMessages(wordId: Int) = messages.exists { _.containsWord(wordId) }
	/**
	 * @param wordId Id of the targeted word
	 * @return Whether this message thread mentions that word at least once
	 */
	def containsWord(wordId: Int) = containsWordInSubjects(wordId) || containsWordInMessages(wordId)
}