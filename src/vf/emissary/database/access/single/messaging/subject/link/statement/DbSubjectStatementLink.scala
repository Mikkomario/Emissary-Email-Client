package vf.emissary.database.access.single.messaging.subject.link.statement

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.SubjectStatementLinkDbFactory
import vf.emissary.database.storable.messaging.SubjectStatementLinkDbModel
import vf.emissary.model.stored.messaging.SubjectStatementLink

/**
  * Used for accessing individual subject statement links
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object DbSubjectStatementLink 
	extends SingleRowModelAccess[SubjectStatementLink] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	private def model = SubjectStatementLinkDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = SubjectStatementLinkDbFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted subject statement link
	  * @return An access point to that subject statement link
	  */
	def apply(id: Int) = DbSingleSubjectStatementLink(id)
	
	/**
	  * @param
	  * 
		 condition Filter condition to apply in addition to this root view's condition. Should yield unique subject statement links.
	  * @return An access point to the subject statement link that satisfies the specified condition
	  */
	private def distinct(condition: Condition) = UniqueSubjectStatementLinkAccess(condition)
}

