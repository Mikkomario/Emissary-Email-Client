package vf.emissary.database.access.single.messaging.subject

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.SubjectFactory
import vf.emissary.database.model.messaging.SubjectModel
import vf.emissary.model.stored.messaging.Subject

/**
  * Used for accessing individual subjects
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbSubject extends SingleRowModelAccess[Subject] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = SubjectModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = SubjectFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted subject
	  * @return An access point to that subject
	  */
	def apply(id: Int) = DbSingleSubject(id)
	
	// TODO: Implement
	// def store()
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique subjects.
	  * @return An access point to the subject that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = UniqueSubjectAccess(mergeCondition(condition))
}

