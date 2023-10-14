package vf.emissary.database.factory.messaging

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.FromRowFactoryWithTimestamps
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.partial.messaging.MessageThreadData
import vf.emissary.model.stored.messaging.MessageThread

/**
  * Used for reading message thread data from the DB
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object MessageThreadFactory 
	extends FromValidatedRowModelFactory[MessageThread] with FromRowFactoryWithTimestamps[MessageThread]
{
	// IMPLEMENTED	--------------------
	
	override def creationTimePropertyName = "created"
	
	override def table = EmissaryTables.messageThread
	
	override protected def fromValidatedModel(valid: Model) = 
		MessageThread(valid("id").getInt, MessageThreadData(valid("created").getInstant))
}

