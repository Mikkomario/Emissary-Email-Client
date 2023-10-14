package vf.emissary.database.factory.messaging

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.partial.messaging.AddressData
import vf.emissary.model.stored.messaging.Address

/**
  * Used for reading address data from the DB
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object AddressFactory extends FromValidatedRowModelFactory[Address]
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def table = EmissaryTables.address
	
	override protected def fromValidatedModel(valid: Model) = 
		Address(valid("id").getInt, AddressData(valid("address").getString, valid("created").getInstant))
}

