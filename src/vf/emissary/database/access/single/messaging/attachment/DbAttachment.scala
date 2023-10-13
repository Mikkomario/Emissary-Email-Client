package vf.emissary.database.access.single.messaging.attachment

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.AttachmentFactory
import vf.emissary.database.model.messaging.AttachmentModel
import vf.emissary.model.stored.messaging.Attachment

/**
  * Used for accessing individual attachments
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
object DbAttachment extends SingleRowModelAccess[Attachment] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = AttachmentModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = AttachmentFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted attachment
	  * @return An access point to that attachment
	  */
	def apply(id: Int) = DbSingleAttachment(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique attachments.
	  * @return An access point to the attachment that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = UniqueAttachmentAccess(mergeCondition(condition))
}

