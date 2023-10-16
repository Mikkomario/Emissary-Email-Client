package vf.emissary.database.access.many.messaging.message_recipient_link

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple message recipient links at a time
  * @author Mikko Hilpinen
  * @since 15.10.2023, v0.1
  */
object DbMessageRecipientLinks extends ManyMessageRecipientLinksAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted message recipient links
	  * @return An access point to message recipient links with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbMessageRecipientLinksSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbMessageRecipientLinksSubset(targetIds: Set[Int]) extends ManyMessageRecipientLinksAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

