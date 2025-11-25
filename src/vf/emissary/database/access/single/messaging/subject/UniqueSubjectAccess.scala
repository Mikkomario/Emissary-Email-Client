package vf.emissary.database.access.single.messaging.subject

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.view.{FilterableView, ViewFactory}
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.SubjectDbFactory
import vf.emissary.model.stored.messaging.Subject

object UniqueSubjectAccess extends ViewFactory[UniqueSubjectAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): UniqueSubjectAccess = _UniqueSubjectAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _UniqueSubjectAccess(override val accessCondition: Option[Condition]) 
		extends UniqueSubjectAccess
}

/**
  * A common trait for access points that return individual and distinct subjects.
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait UniqueSubjectAccess 
	extends UniqueSubjectAccessLike[Subject, UniqueSubjectAccess] with SingleRowModelAccess[Subject] 
		with FilterableView[UniqueSubjectAccess]
{
	// IMPLEMENTED	--------------------
	
	override def factory = SubjectDbFactory
	override def self = this
	
	override def apply(condition: Condition): UniqueSubjectAccess = UniqueSubjectAccess(condition)
}

