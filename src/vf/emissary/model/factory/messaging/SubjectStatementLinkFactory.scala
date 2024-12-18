package vf.emissary.model.factory.messaging

import vf.emissary.model.factory.text.StatementPlacementFactory

/**
  * Common trait for subject statement link-related factories which allow construction 
	with individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait SubjectStatementLinkFactory[+A] extends StatementPlacementFactory[A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param subjectId New subject id to assign
	  * @return Copy of this item with the specified subject id
	  */
	def withSubjectId(subjectId: Int): A
	
	
	// IMPLEMENTED	--------------------
	
	override def withParentId(parentId: Int) = withSubjectId(parentId)
}

