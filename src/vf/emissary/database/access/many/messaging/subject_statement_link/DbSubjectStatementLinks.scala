package vf.emissary.database.access.many.messaging.subject_statement_link

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple subject statement links at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbSubjectStatementLinks extends ManySubjectStatementLinksAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted subject statement links
	  * @return An access point to subject statement links with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbSubjectStatementLinksSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbSubjectStatementLinksSubset(targetIds: Set[Int]) extends ManySubjectStatementLinksAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

