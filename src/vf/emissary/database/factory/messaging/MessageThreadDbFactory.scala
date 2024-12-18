package vf.emissary.database.factory.messaging

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.FromTimelineRowFactory
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.emissary.database.storable.messaging.MessageThreadDbModel
import vf.emissary.model.partial.messaging.MessageThreadData
import vf.emissary.model.stored.messaging.MessageThread

/**
  * Used for reading message thread data from the DB
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object MessageThreadDbFactory 
	extends FromValidatedRowModelFactory[MessageThread] with FromTimelineRowFactory[MessageThread]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	def model = MessageThreadDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = model.table
	
	override def timestamp = model.created
	
	override protected def fromValidatedModel(valid: Model) = 
		MessageThread(valid(this.model.id.name).getInt, 
			MessageThreadData(valid(this.model.created.name).getInstant))
}

