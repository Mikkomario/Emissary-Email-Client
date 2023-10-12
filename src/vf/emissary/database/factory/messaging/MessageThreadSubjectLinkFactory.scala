package vf.emissary.database.factory.messaging

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.FromRowFactoryWithTimestamps
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.partial.messaging.MessageThreadSubjectLinkData
import vf.emissary.model.stored.messaging.MessageThreadSubjectLink

/**
  * Used for reading message thread subject link data from the DB
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object MessageThreadSubjectLinkFactory 
	extends FromValidatedRowModelFactory[MessageThreadSubjectLink] 
		with FromRowFactoryWithTimestamps[MessageThreadSubjectLink]
{
	// IMPLEMENTED	--------------------
	
	override def creationTimePropertyName = "created"
	
	override def table = EmissaryTables.messageThreadSubjectLink
	
	override protected def fromValidatedModel(valid: Model) = 
		MessageThreadSubjectLink(valid("id").getInt, MessageThreadSubjectLinkData(valid("threadId").getInt, 
			valid("subjectId").getInt, valid("created").getInstant))
}

