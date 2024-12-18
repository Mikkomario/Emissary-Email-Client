package vf.emissary.model.factory.messaging

import java.time.Instant

/**
  * Common trait for message thread subject link-related factories which allow construction 
  * with individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait MessageThreadSubjectLinkFactory[+A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param created New created to assign
	  * @return Copy of this item with the specified created
	  */
	def withCreated(created: Instant): A
	
	/**
	  * @param subjectId New subject id to assign
	  * @return Copy of this item with the specified subject id
	  */
	def withSubjectId(subjectId: Int): A
	
	/**
	  * @param threadId New thread id to assign
	  * @return Copy of this item with the specified thread id
	  */
	def withThreadId(threadId: Int): A
}

