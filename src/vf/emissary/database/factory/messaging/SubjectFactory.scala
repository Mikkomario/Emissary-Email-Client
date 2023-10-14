package vf.emissary.database.factory.messaging

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.partial.messaging.SubjectData
import vf.emissary.model.stored.messaging.Subject

/**
  * Used for reading subject data from the DB
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object SubjectFactory extends FromValidatedRowModelFactory[Subject]
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def table = EmissaryTables.subject
	
	override protected def fromValidatedModel(valid: Model) = 
		Subject(valid("id").getInt, SubjectData(valid("created").getInstant))
}

