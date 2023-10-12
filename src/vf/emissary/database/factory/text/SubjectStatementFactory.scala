package vf.emissary.database.factory.text

import utopia.vault.nosql.factory.row.FromRowFactoryWithTimestamps
import utopia.vault.nosql.factory.row.linked.CombiningFactory
import vf.emissary.database.factory.messaging.SubjectStatementLinkFactory
import vf.emissary.model.combined.text.SubjectStatement
import vf.emissary.model.stored.messaging.SubjectStatementLink
import vf.emissary.model.stored.text.Statement

/**
  * Used for reading subject statements from the database
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object SubjectStatementFactory 
	extends CombiningFactory[SubjectStatement, Statement, SubjectStatementLink] 
		with FromRowFactoryWithTimestamps[SubjectStatement]
{
	// IMPLEMENTED	--------------------
	
	override def childFactory = SubjectStatementLinkFactory
	
	override def creationTimePropertyName = StatementFactory.creationTimePropertyName
	
	override def parentFactory = StatementFactory
	
	override def apply(statement: Statement, subjectLink: SubjectStatementLink) = 
		SubjectStatement(statement, subjectLink)
}

