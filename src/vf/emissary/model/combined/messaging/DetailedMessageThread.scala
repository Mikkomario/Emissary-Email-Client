package vf.emissary.model.combined.messaging

import java.time.Instant

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
	
	
	// IMPLEMENTED  -------------------
	
	override def toString = {
		val subjectStr = subject match {
			case Some(subject) => s"$subject:\n\n"
			case None => ""
		}
		s"$subjectStr${messages.mkString("\n\n")}"
	}
}