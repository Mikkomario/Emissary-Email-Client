package vf.emissary.model.combined.messaging

import vf.emissary.model.stored.messaging.Subject

import java.time.Instant

object DetailedSubject
{
	/**
	 * @param subject Subject to wrap
	 * @param statements Statements made within the specified subject
	 * @return Specified subject with the specified statements included
	 */
	def apply(subject: Subject, statements: Vector[DetailedStatement]): DetailedSubject =
		apply(subject.id, statements, subject.created)
}

/**
 * Includes textual information to a subject
 * @author Mikko Hilpinen
 * @since 15.10.2023, v0.1
 */
case class DetailedSubject(id: Int, statements: Vector[DetailedStatement], created: Instant)
{
	// IMPLEMENTED  ----------------------
	
	override def toString = statements.mkString
	
	
	// OTHER    --------------------------
	
	/**
	 * @param wordId Id of the targeted word
	 * @return Whether this subject contains that word
	 */
	def containsWord(wordId: Int) = statements.exists { _.containsWord(wordId) }
}
