package vf.emissary.database.factory.messaging

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import utopia.vault.sql.OrderBy
import vf.emissary.database.storable.messaging.PendingThreadReferenceDbModel
import vf.emissary.model.partial.messaging.PendingThreadReferenceData
import vf.emissary.model.stored.messaging.PendingThreadReference

/**
  * Used for reading pending thread reference data from the DB
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object PendingThreadReferenceDbFactory extends FromValidatedRowModelFactory[PendingThreadReference]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	def model = PendingThreadReferenceDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering: Option[OrderBy] = None
	
	override def table = model.table
	
	override protected def fromValidatedModel(valid: Model) = 
		PendingThreadReference(valid(this.model.id.name).getInt, 
			PendingThreadReferenceData(valid(this.model.threadId.name).getInt, 
			valid(this.model.referencedMessageId.name).getString, valid(this.model.created.name).getInstant))
}

