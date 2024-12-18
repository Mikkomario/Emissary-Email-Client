package vf.emissary.database.factory.messaging

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import utopia.vault.sql.OrderBy
import vf.emissary.database.storable.messaging.AddressNameDbModel
import vf.emissary.model.partial.messaging.AddressNameData
import vf.emissary.model.stored.messaging.AddressName

/**
  * Used for reading address name data from the DB
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object AddressNameDbFactory extends FromValidatedRowModelFactory[AddressName]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	def model = AddressNameDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering: Option[OrderBy] = None
	
	override def table = model.table
	
	override protected def fromValidatedModel(valid: Model) = 
		AddressName(valid(this.model.id.name).getInt, 
			AddressNameData(valid(this.model.addressId.name).getInt, valid(this.model.name.name).getString, 
			valid(this.model.created.name).getInstant, valid(this.model.isSelfAssigned.name).getBoolean))
}

