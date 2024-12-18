package vf.emissary.database.access.single.messaging.reference.pending.thread

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.PendingThreadReferenceDbFactory
import vf.emissary.database.storable.messaging.PendingThreadReferenceDbModel
import vf.emissary.model.stored.messaging.PendingThreadReference

/**
  * Used for accessing individual pending thread references
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object DbPendingThreadReference 
	extends SingleRowModelAccess[PendingThreadReference] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	private def model = PendingThreadReferenceDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = PendingThreadReferenceDbFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted pending thread reference
	  * @return An access point to that pending thread reference
	  */
	def apply(id: Int) = DbSinglePendingThreadReference(id)
	
	/**
	  * @param
	  * 
		 condition Filter condition to apply in addition to this root view's condition. Should yield unique pending thread references.
	  * @return An access point to the pending thread reference that satisfies the specified condition
	  */
	private def distinct(condition: Condition) = UniquePendingThreadReferenceAccess(condition)
}

