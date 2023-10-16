package vf.emissary.database.access.many.messaging.address

import utopia.flow.collection.immutable.Pair
import utopia.flow.operator.EqualsExtensions._
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.view.UnconditionalView
import vf.emissary.model.partial.messaging.AddressData

/**
  * The root access point when targeting multiple addresses at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbAddresses extends ManyAddressesAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted addresses
	  * @return An access point to addresses with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbAddressesSubset(ids)
	
	/**
	 * Stores the specified addresses in the database. Avoids inserting duplicates.
	 * @param addresses Addresses to store
	 * @param connection Implicit DB connection
	 * @return Newly inserted addresses + addresses that already existed in the database
	 */
	def store(addresses: Iterable[String])(implicit connection: Connection) = {
		if (addresses.nonEmpty) {
			// Checks for existing matches
			val existing = matching(addresses).pull
			// Inserts missing addresses, if any (case-insensitive)
			val missing = addresses.filterNot { address => existing.exists { _.address ~== address } }.toVector
			val inserted = model.insert(missing.map { AddressData(_) })
			
			Pair(inserted, existing)
		}
		else
			Pair.twice(Vector.empty)
	}
	
	
	// NESTED	--------------------
	
	class DbAddressesSubset(targetIds: Set[Int]) extends ManyAddressesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

