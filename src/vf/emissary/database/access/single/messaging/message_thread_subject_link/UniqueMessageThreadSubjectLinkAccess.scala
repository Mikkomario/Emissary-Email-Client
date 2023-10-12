package vf.emissary.database.access.single.messaging.message_thread_subject_link

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleChronoRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.MessageThreadSubjectLinkFactory
import vf.emissary.database.model.messaging.MessageThreadSubjectLinkModel
import vf.emissary.model.stored.messaging.MessageThreadSubjectLink

import java.time.Instant

object UniqueMessageThreadSubjectLinkAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueMessageThreadSubjectLinkAccess = 
		new _UniqueMessageThreadSubjectLinkAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueMessageThreadSubjectLinkAccess(condition: Condition) 
		extends UniqueMessageThreadSubjectLinkAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct message thread subject links.
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait UniqueMessageThreadSubjectLinkAccess 
	extends SingleChronoRowModelAccess[MessageThreadSubjectLink, UniqueMessageThreadSubjectLinkAccess] 
		with DistinctModelAccess[MessageThreadSubjectLink, Option[MessageThreadSubjectLink], Value] 
		with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * 
		Id of the thread where the referenced subject was used. None if no message thread subject link (or value)
	  *  was found.
	  */
	def threadId(implicit connection: Connection) = pullColumn(model.threadIdColumn).int
	
	/**
	  * 
		Id of the subject used in the specified thread. None if no message thread subject link (or value) was found.
	  */
	def subjectId(implicit connection: Connection) = pullColumn(model.subjectIdColumn).int
	
	/**
	  * Time when this subject was first used in the specified thread. None if no message
	  *  thread subject link (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.createdColumn).instant
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = MessageThreadSubjectLinkModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageThreadSubjectLinkFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueMessageThreadSubjectLinkAccess = 
		new UniqueMessageThreadSubjectLinkAccess._UniqueMessageThreadSubjectLinkAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the creation times of the targeted message thread subject links
	  * @param newCreated A new created to assign
	  * @return Whether any message thread subject link was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the subject ids of the targeted message thread subject links
	  * @param newSubjectId A new subject id to assign
	  * @return Whether any message thread subject link was affected
	  */
	def subjectId_=(newSubjectId: Int)(implicit connection: Connection) = 
		putColumn(model.subjectIdColumn, newSubjectId)
	
	/**
	  * Updates the thread ids of the targeted message thread subject links
	  * @param newThreadId A new thread id to assign
	  * @return Whether any message thread subject link was affected
	  */
	def threadId_=(newThreadId: Int)(implicit connection: Connection) = 
		putColumn(model.threadIdColumn, newThreadId)
}

