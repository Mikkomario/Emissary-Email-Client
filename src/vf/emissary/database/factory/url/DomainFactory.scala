package vf.emissary.database.factory.url

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.partial.url.DomainData
import vf.emissary.model.stored.url.Domain

/**
  * Used for reading domain data from the DB
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
object DomainFactory extends FromValidatedRowModelFactory[Domain]
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def table = EmissaryTables.domain
	
	override protected def fromValidatedModel(valid: Model) = 
		Domain(valid("id").getInt, DomainData(valid("url").getString, valid("created").getInstant))
}

