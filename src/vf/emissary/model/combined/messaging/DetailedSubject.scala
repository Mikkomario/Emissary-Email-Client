package vf.emissary.model.combined.messaging

import java.time.Instant

/**
 * Includes textual information to a subject
 * @author Mikko Hilpinen
 * @since 15.10.2023, v0.1
 */
case class DetailedSubject(id: Int, statements: Vector[DetailedStatement], created: Instant)
{
	override def toString = statements.mkString
}
