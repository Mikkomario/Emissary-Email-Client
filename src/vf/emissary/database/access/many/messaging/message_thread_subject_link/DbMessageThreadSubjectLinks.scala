package vf.emissary.database.access.many.messaging.message_thread_subject_link

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple message thread subject links at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbMessageThreadSubjectLinks extends ManyMessageThreadSubjectLinksAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted message thread subject links
	  * @return An access point to message thread subject links with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbMessageThreadSubjectLinksSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbMessageThreadSubjectLinksSubset(targetIds: Set[Int]) extends ManyMessageThreadSubjectLinksAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(index in targetIds)
	}
}

