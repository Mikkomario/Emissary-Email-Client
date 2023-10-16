package vf.emissary.database.factory.url

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.partial.url.LinkPlacementData
import vf.emissary.model.stored.url.LinkPlacement

/**
  * Used for reading link placement data from the DB
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
object LinkPlacementFactory extends FromValidatedRowModelFactory[LinkPlacement]
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def table = EmissaryTables.linkPlacement
	
	override protected def fromValidatedModel(valid: Model) = 
		LinkPlacement(valid("id").getInt, LinkPlacementData(valid("statementId").getInt, 
			valid("linkId").getInt, valid("orderIndex").getInt))
}

