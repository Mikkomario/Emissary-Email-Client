package vf.emissary.database.factory.messaging

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.partial.messaging.MessageStatementLinkData
import vf.emissary.model.stored.messaging.MessageStatementLink

/**
  * Used for reading message statement link data from the DB
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object MessageStatementLinkFactory extends FromValidatedRowModelFactory[MessageStatementLink]
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def table = EmissaryTables.messageStatementLink
	
	override protected def fromValidatedModel(valid: Model) = 
		MessageStatementLink(valid("id").getInt, MessageStatementLinkData(valid("messageId").getInt, 
			valid("statementId").getInt, valid("orderIndex").getInt))
}

