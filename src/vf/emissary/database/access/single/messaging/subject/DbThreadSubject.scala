package vf.emissary.database.access.single.messaging.subject

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.ThreadSubjectFactory
import vf.emissary.database.model.messaging.{MessageThreadSubjectLinkModel, SubjectModel}
import vf.emissary.model.combined.messaging.ThreadSubject

/**
  * Used for accessing individual thread subjects
  * @author Mikko Hilpinen
  * @since 17.10.2023, v0.1
  */
object DbThreadSubject extends SingleRowModelAccess[ThreadSubject] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * A database model (factory) used for interacting with linked subjects
	  */
	protected def model = SubjectModel
	
	/**
	  * A database model (factory) used for interacting with the linked thread link
	  */
	protected def threadLinkModel = MessageThreadSubjectLinkModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = ThreadSubjectFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted thread subject
	  * @return An access point to that thread subject
	  */
	def apply(id: Int) = DbSingleThreadSubject(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique thread subjects.
	  * @return An access point to the thread subject that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = UniqueThreadSubjectAccess(mergeCondition(condition))
}

