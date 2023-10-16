package vf.emissary.database.factory.messaging

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.enumeration.RecipientType
import vf.emissary.model.partial.messaging.MessageRecipientLinkData
import vf.emissary.model.stored.messaging.MessageRecipientLink

/**
  * Used for reading message recipient link data from the DB
  * @author Mikko Hilpinen
  * @since 15.10.2023, v0.1
  */
object MessageRecipientLinkFactory extends FromValidatedRowModelFactory[MessageRecipientLink]
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def table = EmissaryTables.messageRecipientLink
	
	override protected def fromValidatedModel(valid: Model) = 
		MessageRecipientLink(valid("id").getInt, MessageRecipientLinkData(valid("messageId").getInt, 
			valid("recipientId").getInt, RecipientType.fromValue(valid("roleId"))))
}

