package vf.emissary.database.factory.messaging

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.FromRowFactoryWithTimestamps
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.partial.messaging.MessageData
import vf.emissary.model.stored.messaging.Message

/**
  * Used for reading message data from the DB
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object MessageFactory extends FromValidatedRowModelFactory[Message] with FromRowFactoryWithTimestamps[Message]
{
	// IMPLEMENTED	--------------------
	
	override def creationTimePropertyName = "created"
	
	override def table = EmissaryTables.message
	
	override protected def fromValidatedModel(valid: Model) = 
		Message(valid("id").getInt, MessageData(valid("threadId").getInt, valid("senderId").getInt, 
			valid("messageId").getString, valid("replyToId").int, valid("created").getInstant))
}

