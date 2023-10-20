package vf.emissary.database.factory.messaging

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.partial.messaging.PendingReplyReferenceData
import vf.emissary.model.stored.messaging.PendingReplyReference

/**
  * Used for reading pending reply reference data from the DB
  * @author Mikko Hilpinen
  * @since 19.10.2023, v0.1
  */
object PendingReplyReferenceFactory extends FromValidatedRowModelFactory[PendingReplyReference]
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def table = EmissaryTables.pendingReplyReference
	
	override protected def fromValidatedModel(valid: Model) = 
		PendingReplyReference(valid("id").getInt, PendingReplyReferenceData(valid("messageId").getInt, 
			valid("referencedMessageId").getString, valid("created").getInstant))
}

