package vf.emissary.database.factory.messaging

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import utopia.vault.sql.OrderBy
import vf.emissary.database.storable.messaging.EmailServiceUserDbModel
import vf.emissary.model.partial.messaging.EmailServiceUserData
import vf.emissary.model.stored.messaging.EmailServiceUser

/**
  * Used for reading email service user data from the DB
  * @author Mikko Hilpinen
  * @since 22.12.2024, v1.1
  */
object EmailServiceUserDbFactory extends FromValidatedRowModelFactory[EmailServiceUser]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	val model = EmailServiceUserDbModel
	
	override lazy val defaultOrdering: Option[OrderBy] = None
	
	
	// IMPLEMENTED	--------------------
	
	override def table = model.table
	
	override protected def fromValidatedModel(valid: Model) = 
		EmailServiceUser(valid(this.model.id.name).getInt, 
			EmailServiceUserData(valid(this.model.serviceId.name).getInt, 
			valid(this.model.addressId.name).getInt, valid(this.model.password.name).getString, 
			valid(this.model.created.name).getInstant))
}

