package vf.emissary.database.access.many.messaging.subject.link.statement

import utopia.flow.collection.immutable.IntSet
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.emissary.database.access.many.text.placement.statement.ManyStatementPlacementsAccessLike
import vf.emissary.database.factory.messaging.SubjectStatementLinkDbFactory
import vf.emissary.database.storable.messaging.SubjectStatementLinkDbModel
import vf.emissary.model.stored.messaging.SubjectStatementLink

object ManySubjectStatementLinksAccess extends ViewFactory[ManySubjectStatementLinksAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManySubjectStatementLinksAccess = 
		_ManySubjectStatementLinksAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManySubjectStatementLinksAccess(override val accessCondition: Option[Condition]) 
		extends ManySubjectStatementLinksAccess
}

/**
  * A common trait for access points which target multiple subject statement links at a time
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait ManySubjectStatementLinksAccess 
	extends ManyStatementPlacementsAccessLike[SubjectStatementLink, ManySubjectStatementLinksAccess] 
		with ManyRowModelAccess[SubjectStatementLink]
{
	// COMPUTED	--------------------
	
	/**
	  * subject ids of the accessible subject statement links
	  */
	def subjectIds(implicit connection: Connection) = parentIds
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = SubjectStatementLinkDbFactory
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	override protected def model = SubjectStatementLinkDbModel
	
	override protected def self = this
	
	override def apply(condition: Condition): ManySubjectStatementLinksAccess = 
		ManySubjectStatementLinksAccess(condition)
	
	
	// OTHER	--------------------
	
	/**
	  * @param subjectId subject id to target
	  * @return Copy of this access point that only includes subject statement links 
		with the specified subject id
	  */
	def withinSubject(subjectId: Int) = filter(model.subjectId.column <=> subjectId)
	
	/**
	  * @param subjectIds Targeted subject ids
	  * @return
	  * 
		 Copy of this access point that only includes subject statement links where subject id is within the specified value set
	  */
	def withinSubjects(subjectIds: IterableOnce[Int]) = filter(model
		.subjectId.column.in(IntSet.from(subjectIds)))
}

