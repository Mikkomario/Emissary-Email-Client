package vf.emissary.model.combined.text

import utopia.logos.model.combined.text.{CombinedStatement, PlacedStatementLike}
import utopia.logos.model.stored.text.StoredStatement
import vf.emissary.model.partial.messaging.SubjectStatementLinkData
import vf.emissary.model.stored.messaging.SubjectStatementLink

/**
  * Represents a statement made within a specific message (thread) subject
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class SubjectStatement(statement: StoredStatement, subjectLink: SubjectStatementLink)
	extends CombinedStatement[SubjectStatement]
		with PlacedStatementLike[SubjectStatement, SubjectStatementLink, SubjectStatementLinkData]
{
	// IMPLEMENTED	--------------------
	
	override def placement: SubjectStatementLink = subjectLink
	
	override protected def wrap(factory: StoredStatement): SubjectStatement = copy(statement = factory)
}

