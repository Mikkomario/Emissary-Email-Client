package vf.emissary.database.factory.messaging

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import utopia.vault.sql.OrderBy
import vf.emissary.database.storable.messaging.PendingReplyReferenceDbModel
import vf.emissary.model.partial.messaging.PendingReplyReferenceData
import vf.emissary.model.stored.messaging.PendingReplyReference

/**
  * Used for reading pending reply reference data from the DB
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object PendingReplyReferenceDbFactory extends FromValidatedRowModelFactory[PendingReplyReference]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	def model = PendingReplyReferenceDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering: Option[OrderBy] = None
	
	override def table = model.table
	
	override protected def fromValidatedModel(valid: Model) = 
		PendingReplyReference(valid(this.model.id.name).getInt, 
			PendingReplyReferenceData(valid(this.model.messageId.name).getInt, 
			valid(this.model.referencedMessageId.name).getString, valid(this.model.created.name).getInstant))
}

