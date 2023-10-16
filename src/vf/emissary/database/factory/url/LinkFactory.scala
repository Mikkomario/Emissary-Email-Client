package vf.emissary.database.factory.url

import utopia.bunnymunch.jawn.JsonBunny
import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.partial.url.LinkData
import vf.emissary.model.stored.url.Link

/**
  * Used for reading link data from the DB
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
object LinkFactory extends FromValidatedRowModelFactory[Link]
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def table = EmissaryTables.link
	
	override protected def fromValidatedModel(valid: Model) = 
		Link(valid("id").getInt, LinkData(valid("requestPathId").getInt, 
			valid("queryParameters").notEmpty match { case Some(v) => JsonBunny.sureMunch(v.getString).getModel; case None => Model.empty }, 
			valid("created").getInstant))
}

