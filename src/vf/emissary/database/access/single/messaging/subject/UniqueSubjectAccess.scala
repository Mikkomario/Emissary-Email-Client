package vf.emissary.database.access.single.messaging.subject

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.SubjectFactory
import vf.emissary.model.stored.messaging.Subject

object UniqueSubjectAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueSubjectAccess = new _UniqueSubjectAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueSubjectAccess(condition: Condition) extends UniqueSubjectAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct subjects.
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait UniqueSubjectAccess 
	extends UniqueSubjectAccessLike[Subject] with SingleRowModelAccess[Subject] 
		with FilterableView[UniqueSubjectAccess]
{
	// IMPLEMENTED	--------------------
	
	override def factory = SubjectFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueSubjectAccess = 
		new UniqueSubjectAccess._UniqueSubjectAccess(mergeCondition(filterCondition))
}

