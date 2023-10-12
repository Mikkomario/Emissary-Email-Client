package vf.emissary.model.combined.text

import utopia.flow.view.template.Extender
import vf.emissary.model.partial.text.StatementData
import vf.emissary.model.stored.messaging.SubjectStatementLink
import vf.emissary.model.stored.text.Statement

/**
  * Represents a statement made within a specific message (thread) subject
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class SubjectStatement(statement: Statement, subjectLink: SubjectStatementLink) 
	extends Extender[StatementData]
{
	// COMPUTED	--------------------
	
	/**
	  * Id of this statement in the database
	  */
	def id = statement.id
	
	
	// IMPLEMENTED	--------------------
	
	override def wrapped = statement.data
}

