package vf.emissary.model.factory.messaging

import vf.emissary.model.factory.text.StatementPlacementFactoryWrapper

/**
  * Common
  * 
	 trait for classes that implement SubjectStatementLinkFactory by wrapping a SubjectStatementLinkFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait SubjectStatementLinkFactoryWrapper[A <: SubjectStatementLinkFactory[A], +Repr] 
	extends SubjectStatementLinkFactory[Repr] with StatementPlacementFactoryWrapper[A, Repr]
{
	// IMPLEMENTED	--------------------
	
	override def withSubjectId(subjectId: Int) = withParentId(subjectId)
}

