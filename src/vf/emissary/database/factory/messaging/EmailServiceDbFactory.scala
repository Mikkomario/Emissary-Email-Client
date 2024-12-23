package vf.emissary.database.factory.messaging

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import utopia.vault.sql.OrderBy
import vf.emissary.database.storable.messaging.EmailServiceDbModel
import vf.emissary.model.partial.messaging.EmailServiceData
import vf.emissary.model.stored.messaging.EmailService

/**
  * Used for reading email service data from the DB
  * @author Mikko Hilpinen
  * @since 22.12.2024, v1.1
  */
object EmailServiceDbFactory extends FromValidatedRowModelFactory[EmailService]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	val model = EmailServiceDbModel
	
	override lazy val defaultOrdering: Option[OrderBy] = None
	
	
	// IMPLEMENTED	--------------------
	
	override def table = model.table
	
	override protected def fromValidatedModel(valid: Model) = 
		EmailService(valid(this.model.id.name).getInt, 
			EmailServiceData(valid(this.model.address.name).getString, 
			valid(this.model.created.name).getInstant, valid(this.model.name.name).getString))
}

