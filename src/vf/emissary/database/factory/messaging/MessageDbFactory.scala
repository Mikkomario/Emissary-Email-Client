package vf.emissary.database.factory.messaging

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.FromTimelineRowFactory
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.emissary.database.storable.messaging.MessageDbModel
import vf.emissary.model.partial.messaging.MessageData
import vf.emissary.model.stored.messaging.Message

/**
  * Used for reading message data from the DB
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object MessageDbFactory extends FromValidatedRowModelFactory[Message] with FromTimelineRowFactory[Message]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	def model = MessageDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = model.table
	
	override def timestamp = model.created
	
	override protected def fromValidatedModel(valid: Model) = 
		Message(valid(this.model.id.name).getInt, MessageData(valid(this.model.threadId.name).getInt, 
			valid(this.model.senderId.name).getInt, valid(this.model.messageId.name).getString, 
			valid(this.model.replyToId.name).int, valid(this.model.created.name).getInstant))
}

