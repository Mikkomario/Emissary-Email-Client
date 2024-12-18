package vf.emissary.database.factory.messaging

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import utopia.vault.sql.OrderBy
import vf.emissary.database.storable.messaging.AttachmentDbModel
import vf.emissary.model.partial.messaging.AttachmentData
import vf.emissary.model.stored.messaging.Attachment

/**
  * Used for reading attachment data from the DB
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object AttachmentDbFactory extends FromValidatedRowModelFactory[Attachment]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	def model = AttachmentDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering: Option[OrderBy] = None
	
	override def table = model.table
	
	override protected def fromValidatedModel(valid: Model) = 
		Attachment(valid(this.model.id.name).getInt, AttachmentData(valid(this.model.messageId.name).getInt, 
			valid(this.model.fileName.name).getString))
}

