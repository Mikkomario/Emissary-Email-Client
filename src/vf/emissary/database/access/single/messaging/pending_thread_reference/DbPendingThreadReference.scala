package vf.emissary.database.access.single.messaging.pending_thread_reference

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.PendingThreadReferenceFactory
import vf.emissary.database.model.messaging.PendingThreadReferenceModel
import vf.emissary.model.stored.messaging.PendingThreadReference

/**
  * Used for accessing individual pending thread references
  * @author Mikko Hilpinen
  * @since 19.10.2023, v0.1
  */
object DbPendingThreadReference 
	extends SingleRowModelAccess[PendingThreadReference] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = PendingThreadReferenceModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = PendingThreadReferenceFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted pending thread reference
	  * @return An access point to that pending thread reference
	  */
	def apply(id: Int) = DbSinglePendingThreadReference(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique pending thread references.
	  * @return An access point to the pending thread reference that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = 
		UniquePendingThreadReferenceAccess(mergeCondition(condition))
}

