package vf.emissary.database.factory.messaging

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import utopia.vault.sql.OrderBy
import vf.emissary.database.storable.messaging.AttachmentMessageLinkDbModel
import vf.emissary.model.partial.messaging.AttachmentMessageLinkData
import vf.emissary.model.stored.messaging.AttachmentMessageLink

/**
  * Used for reading attachment message link data from the DB
  * @author Mikko Hilpinen
  * @since 27.12.2024, v1.1
  */
object AttachmentMessageLinkDbFactory extends FromValidatedRowModelFactory[AttachmentMessageLink]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	val model = AttachmentMessageLinkDbModel
	
	override lazy val defaultOrdering: Option[OrderBy] = None
	
	
	// IMPLEMENTED	--------------------
	
	override def table = model.table
	
	override protected def fromValidatedModel(valid: Model) = 
		AttachmentMessageLink(valid(this.model.id.name).getInt, 
			AttachmentMessageLinkData(valid(this.model.attachmentId.name).getInt, 
			valid(this.model.messageId.name).getInt))
}

