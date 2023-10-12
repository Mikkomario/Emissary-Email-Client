package vf.emissary.database.factory.text

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.partial.text.DelimiterData
import vf.emissary.model.stored.text.Delimiter

/**
  * Used for reading delimiter data from the DB
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DelimiterFactory extends FromValidatedRowModelFactory[Delimiter]
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def table = EmissaryTables.delimiter
	
	override protected def fromValidatedModel(valid: Model) = 
		Delimiter(valid("id").getInt, DelimiterData(valid("text").getString, valid("created").getInstant))
}

