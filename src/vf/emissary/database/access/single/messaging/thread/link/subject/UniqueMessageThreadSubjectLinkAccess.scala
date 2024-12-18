package vf.emissary.database.access.single.messaging.thread.link.subject

import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleChronoRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{FilterableView, ViewFactory}
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.MessageThreadSubjectLinkDbFactory
import vf.emissary.database.storable.messaging.MessageThreadSubjectLinkDbModel
import vf.emissary.model.stored.messaging.MessageThreadSubjectLink

object UniqueMessageThreadSubjectLinkAccess extends ViewFactory[UniqueMessageThreadSubjectLinkAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): UniqueMessageThreadSubjectLinkAccess = 
		_UniqueMessageThreadSubjectLinkAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _UniqueMessageThreadSubjectLinkAccess(override val accessCondition: Option[Condition]) 
		extends UniqueMessageThreadSubjectLinkAccess
}

/**
  * A common trait for access points that return individual and distinct message thread subject links.
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait UniqueMessageThreadSubjectLinkAccess 
	extends SingleChronoRowModelAccess[MessageThreadSubjectLink, UniqueMessageThreadSubjectLinkAccess] 
		with DistinctModelAccess[MessageThreadSubjectLink, Option[MessageThreadSubjectLink], Value] 
		with FilterableView[UniqueMessageThreadSubjectLinkAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the thread where the referenced subject was used. 
	  * None if no message thread subject link (or value) was found.
	  */
	def threadId(implicit connection: Connection) = pullColumn(model.threadId.column).int
	
	/**
	  * Id of the subject used in the specified thread. 
	  * None if no message thread subject link (or value) was found.
	  */
	def subjectId(implicit connection: Connection) = pullColumn(model.subjectId.column).int
	
	/**
	  * Time when this subject was first used in the specified thread. 
	  * None if no message thread subject link (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.created.column).instant
	
	/**
	  * Unique
	  * 
		 id of the accessible message thread subject link. None if no message thread subject link was accessible.
	  */
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = MessageThreadSubjectLinkDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageThreadSubjectLinkDbFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): UniqueMessageThreadSubjectLinkAccess = 
		UniqueMessageThreadSubjectLinkAccess(condition)
}

