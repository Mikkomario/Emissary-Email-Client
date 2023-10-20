package vf.emissary.database.access.single.messaging.pending_reply_reference

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.PendingReplyReferenceFactory
import vf.emissary.database.model.messaging.PendingReplyReferenceModel
import vf.emissary.model.stored.messaging.PendingReplyReference

/**
  * Used for accessing individual pending reply references
  * @author Mikko Hilpinen
  * @since 19.10.2023, v0.1
  */
object DbPendingReplyReference 
	extends SingleRowModelAccess[PendingReplyReference] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = PendingReplyReferenceModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = PendingReplyReferenceFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted pending reply reference
	  * @return An access point to that pending reply reference
	  */
	def apply(id: Int) = DbSinglePendingReplyReference(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique pending reply references.
	  * @return An access point to the pending reply reference that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = 
		UniquePendingReplyReferenceAccess(mergeCondition(condition))
}

