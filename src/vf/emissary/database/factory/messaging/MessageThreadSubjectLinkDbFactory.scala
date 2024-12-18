package vf.emissary.database.factory.messaging

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.FromTimelineRowFactory
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.emissary.database.storable.messaging.MessageThreadSubjectLinkDbModel
import vf.emissary.model.partial.messaging.MessageThreadSubjectLinkData
import vf.emissary.model.stored.messaging.MessageThreadSubjectLink

/**
  * Used for reading message thread subject link data from the DB
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object MessageThreadSubjectLinkDbFactory 
	extends FromValidatedRowModelFactory[MessageThreadSubjectLink] 
		with FromTimelineRowFactory[MessageThreadSubjectLink]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	def model = MessageThreadSubjectLinkDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def table = model.table
	
	override def timestamp = model.created
	
	override protected def fromValidatedModel(valid: Model) = 
		MessageThreadSubjectLink(valid(this.model.id.name).getInt, 
			MessageThreadSubjectLinkData(valid(this.model.threadId.name).getInt, 
			valid(this.model.subjectId.name).getInt, valid(this.model.created.name).getInstant))
}

