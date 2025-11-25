package vf.emissary.database.access.single.messaging.subject.link.statement

import utopia.logos.database.access.single.text.statement.placement.UniqueStatementPlacementAccessLike
import utopia.vault.database.Connection
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.SubjectStatementLinkDbFactory
import vf.emissary.database.storable.messaging.SubjectStatementLinkDbModel
import vf.emissary.model.stored.messaging.SubjectStatementLink

object UniqueSubjectStatementLinkAccess extends ViewFactory[UniqueSubjectStatementLinkAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): UniqueSubjectStatementLinkAccess = 
		_UniqueSubjectStatementLinkAccess(condition)
	
	
	// NESTED	--------------------
	
	private case class _UniqueSubjectStatementLinkAccess(override val condition: Condition)
		extends UniqueSubjectStatementLinkAccess
}

/**
  * A common trait for access points that return individual and distinct subject statement links.
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait UniqueSubjectStatementLinkAccess 
	extends UniqueStatementPlacementAccessLike[SubjectStatementLink, UniqueSubjectStatementLinkAccess]
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the described subject. 
	  * None if no subject statement link (or value) was found.
	  */
	def subjectId(implicit connection: Connection) = parentId
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = SubjectStatementLinkDbFactory
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	override protected def model = SubjectStatementLinkDbModel
	
	override def self = this
	
	override def apply(condition: Condition): UniqueSubjectStatementLinkAccess = 
		UniqueSubjectStatementLinkAccess(condition)
}

