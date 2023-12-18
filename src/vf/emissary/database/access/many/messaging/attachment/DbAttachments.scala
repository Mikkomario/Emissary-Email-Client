package vf.emissary.database.access.many.messaging.attachment

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple attachments at a time
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
object DbAttachments extends ManyAttachmentsAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted attachments
	  * @return An access point to attachments with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbAttachmentsSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbAttachmentsSubset(targetIds: Set[Int]) extends ManyAttachmentsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(index in targetIds)
	}
}

