package vf.emissary.database.factory.messaging

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import utopia.vault.sql.OrderBy
import vf.emissary.database.storable.messaging.MessageRecipientLinkDbModel
import vf.emissary.model.enumeration.RecipientType
import vf.emissary.model.partial.messaging.MessageRecipientLinkData
import vf.emissary.model.stored.messaging.MessageRecipientLink

/**
  * Used for reading message recipient link data from the DB
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object MessageRecipientLinkDbFactory extends FromValidatedRowModelFactory[MessageRecipientLink]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	def model = MessageRecipientLinkDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering: Option[OrderBy] = None
	
	override def table = model.table
	
	override protected def fromValidatedModel(valid: Model) = 
		MessageRecipientLink(valid(this.model.id.name).getInt, 
			MessageRecipientLinkData(valid(this.model.messageId.name).getInt, 
			valid(this.model.recipientId.name).getInt, RecipientType.fromValue(valid(this.model.role.name))))
}

