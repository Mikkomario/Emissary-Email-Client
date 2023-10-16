package vf.emissary.database.factory.url

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.partial.url.RequestPathData
import vf.emissary.model.stored.url.RequestPath

/**
  * Used for reading request path data from the DB
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
object RequestPathFactory extends FromValidatedRowModelFactory[RequestPath]
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def table = EmissaryTables.requestPath
	
	override protected def fromValidatedModel(valid: Model) = 
		RequestPath(valid("id").getInt, RequestPathData(valid("domainId").getInt, valid("path").getString, 
			valid("created").getInstant))
}

