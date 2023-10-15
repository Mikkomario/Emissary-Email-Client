package vf.emissary.database.factory.messaging

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.partial.messaging.AttachmentData
import vf.emissary.model.stored.messaging.Attachment

/**
  * Used for reading attachment data from the DB
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
object AttachmentFactory extends FromValidatedRowModelFactory[Attachment]
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def table = EmissaryTables.attachment
	
	override protected def fromValidatedModel(valid: Model) = 
		Attachment(valid("id").getInt, AttachmentData(valid("messageId").getInt, valid("fileName").getString))
}

