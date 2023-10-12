package vf.emissary.database.factory.messaging

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.partial.messaging.SubjectStatementLinkData
import vf.emissary.model.stored.messaging.SubjectStatementLink

/**
  * Used for reading subject statement link data from the DB
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object SubjectStatementLinkFactory extends FromValidatedRowModelFactory[SubjectStatementLink]
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def table = EmissaryTables.subjectStatementLink
	
	override protected def fromValidatedModel(valid: Model) = 
		SubjectStatementLink(valid("id").getInt, SubjectStatementLinkData(valid("subjectId").getInt, 
			valid("statementId").getInt, valid("orderIndex").getInt))
}

