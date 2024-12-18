package vf.emissary.database.access.single.messaging.reference.pending.reply

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.PendingReplyReferenceDbFactory
import vf.emissary.database.storable.messaging.PendingReplyReferenceDbModel
import vf.emissary.model.stored.messaging.PendingReplyReference

/**
  * Used for accessing individual pending reply references
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object DbPendingReplyReference 
	extends SingleRowModelAccess[PendingReplyReference] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	private def model = PendingReplyReferenceDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = PendingReplyReferenceDbFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted pending reply reference
	  * @return An access point to that pending reply reference
	  */
	def apply(id: Int) = DbSinglePendingReplyReference(id)
	
	/**
	  * @param
	  * 
		 condition Filter condition to apply in addition to this root view's condition. Should yield unique pending reply references.
	  * @return An access point to the pending reply reference that satisfies the specified condition
	  */
	private def distinct(condition: Condition) = UniquePendingReplyReferenceAccess(condition)
}

