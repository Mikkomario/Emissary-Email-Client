package vf.emissary.database.access.single.messaging.subject_statement_link

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.SubjectStatementLinkFactory
import vf.emissary.database.model.messaging.SubjectStatementLinkModel
import vf.emissary.model.stored.messaging.SubjectStatementLink

/**
  * Used for accessing individual subject statement links
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbSubjectStatementLink 
	extends SingleRowModelAccess[SubjectStatementLink] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = SubjectStatementLinkModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = SubjectStatementLinkFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted subject statement link
	  * @return An access point to that subject statement link
	  */
	def apply(id: Int) = DbSingleSubjectStatementLink(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique subject statement links.
	  * @return An access point to the subject statement link that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = 
		UniqueSubjectStatementLinkAccess(mergeCondition(condition))
}

