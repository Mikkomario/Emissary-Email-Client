package vf.emissary.database.factory.messaging

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.partial.messaging.PendingThreadReferenceData
import vf.emissary.model.stored.messaging.PendingThreadReference

/**
  * Used for reading pending thread reference data from the DB
  * @author Mikko Hilpinen
  * @since 19.10.2023, v0.1
  */
object PendingThreadReferenceFactory extends FromValidatedRowModelFactory[PendingThreadReference]
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def table = EmissaryTables.pendingThreadReference
	
	override protected def fromValidatedModel(valid: Model) = 
		PendingThreadReference(valid("id").getInt, PendingThreadReferenceData(valid("threadId").getInt, 
			valid("referencedMessageId").getString, valid("created").getInstant))
}

