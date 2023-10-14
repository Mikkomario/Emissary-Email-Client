package vf.emissary.database.factory.messaging

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.partial.messaging.AddressNameData
import vf.emissary.model.stored.messaging.AddressName

/**
  * Used for reading address name data from the DB
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
object AddressNameFactory extends FromValidatedRowModelFactory[AddressName]
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def table = EmissaryTables.addressName
	
	override protected def fromValidatedModel(valid: Model) = 
		AddressName(valid("id").getInt, AddressNameData(valid("addressId").getInt, valid("name").getString, 
			valid("created").getInstant, valid("isSelfAssigned").getBoolean))
}

