package vf.emissary.database.factory.text

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.partial.text.WordPlacementData
import vf.emissary.model.stored.text.WordPlacement

/**
  * Used for reading word placement data from the DB
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object WordPlacementFactory extends FromValidatedRowModelFactory[WordPlacement]
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def table = EmissaryTables.wordPlacement
	
	override protected def fromValidatedModel(valid: Model) = 
		WordPlacement(valid("id").getInt, WordPlacementData(valid("statementId").getInt, 
			valid("wordId").getInt, valid("orderIndex").getInt))
}

