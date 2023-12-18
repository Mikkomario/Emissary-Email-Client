package vf.emissary.database.access.many.messaging.message_statement_link

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple message statement links at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbMessageStatementLinks extends ManyMessageStatementLinksAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted message statement links
	  * @return An access point to message statement links with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbMessageStatementLinksSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbMessageStatementLinksSubset(targetIds: Set[Int]) extends ManyMessageStatementLinksAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(index in targetIds)
	}
}

