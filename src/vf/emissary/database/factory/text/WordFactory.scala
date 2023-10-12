package vf.emissary.database.factory.text

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.partial.text.WordData
import vf.emissary.model.stored.text.Word

/**
  * Used for reading word data from the DB
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object WordFactory extends FromValidatedRowModelFactory[Word]
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def table = EmissaryTables.word
	
	override protected def fromValidatedModel(valid: Model) = 
		Word(valid("id").getInt, WordData(valid("text").getString, valid("created").getInstant))
}

