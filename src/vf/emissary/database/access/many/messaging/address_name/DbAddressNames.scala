package vf.emissary.database.access.many.messaging.address_name

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.view.UnconditionalView
import vf.emissary.model.partial.messaging.AddressNameData

/**
  * The root access point when targeting multiple address names at a time
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
object DbAddressNames extends ManyAddressNamesAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted address names
	  * @return An access point to address names with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbAddressNamesSubset(ids)
	
	/**
	 * Assigns specified names to specified addresses
	 * @param namesPerAddressIds Name-assignments grouped by the address whom they concern.
	 *                           Each name is coupled with a boolean that indicates
	 *                           whether the name is self-assigned or not.
	 * @param connection Implicit DB connection
	 */
	def assign(namesPerAddressIds: Map[Int, Seq[(String, Boolean)]])(implicit connection: Connection): Unit = {
		if (namesPerAddressIds.exists { _._2.nonEmpty }) {
			// Checks for existing name assignments
			val existingPerAddressId = forAddresses(namesPerAddressIds.keys).pull.groupBy { _.addressId }
			
			// Groups data to new inserts and self-assigned status updates
			val (newNameData, newSelfAssignedIds) = namesPerAddressIds.splitFlatMap { case (addressId, names) =>
				val existing = existingPerAddressId.getOrElse(addressId, Vector.empty)
				val newNameData = names.filterNot { case (name, _) => existing.exists { _.name == name } }
					.map { case (name, selfAssigned) => AddressNameData(addressId, name, isSelfAssigned = selfAssigned) }
				val newSelfAssignedIds = names.filter { _._2 }.flatMap { case (name, _) =>
					existing.find { a => a.name == name && a.isNotSelfAssigned }.map { _.id }
				}
				
				newNameData -> newSelfAssignedIds
			}
			
			// Applies the updates and the inserts
			if (newSelfAssignedIds.nonEmpty)
				apply(newSelfAssignedIds.toSet).areSelfAssigned = true
			model.insert(newNameData)
		}
	}
	
	
	// NESTED	--------------------
	
	class DbAddressNamesSubset(targetIds: Set[Int]) extends ManyAddressNamesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(index in targetIds)
	}
}

