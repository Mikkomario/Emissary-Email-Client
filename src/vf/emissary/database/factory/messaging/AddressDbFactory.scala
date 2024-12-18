package vf.emissary.database.factory.messaging

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import utopia.vault.sql.OrderBy
import vf.emissary.database.storable.messaging.AddressDbModel
import vf.emissary.model.partial.messaging.AddressData
import vf.emissary.model.stored.messaging.Address

/**
  * Used for reading address data from the DB
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object AddressDbFactory extends FromValidatedRowModelFactory[Address]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	def model = AddressDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering: Option[OrderBy] = None
	
	override def table = model.table
	
	override protected def fromValidatedModel(valid: Model) = 
		Address(valid(this.model.id.name).getInt, AddressData(valid(this.model.address.name).getString, 
			valid(this.model.created.name).getInstant))
}

