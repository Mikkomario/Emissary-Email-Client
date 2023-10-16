package vf.emissary.model.combined.messaging

import utopia.flow.view.template.Extender
import vf.emissary.model.partial.text.StatementData
import vf.emissary.model.stored.text.{Delimiter, Statement}

/**
 * Contains full statement information:
 * - Included words and their placements
 * - Included delimiter
 * @author Mikko Hilpinen
 * @since 15.10.2023, v0.1
 */
case class DetailedStatement(statement: Statement, wordPlacements: Vector[DetailedWordPlacement],
                             delimiter: Option[Delimiter])
	extends Extender[StatementData]
{
	// COMPUTED ---------------------------
	
	/**
	 * @return Id of this statement in the database
	 */
	def id = statement.id
	
	
	// IMPLEMENTED  -----------------------
	
	override def wrapped: StatementData = statement.data
	
	override def toString = {
		val delimiterStr = delimiter match {
			case Some(d) => d.text
			case None => ""
		}
		s"${wordPlacements.mkString(" ")}$delimiterStr"
	}
}
