package vf.emissary.database.access.single.messaging.subject

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.ThreadSubjectFactory
import vf.emissary.database.model.messaging.MessageThreadSubjectLinkModel
import vf.emissary.model.combined.messaging.ThreadSubject

import java.time.Instant

object UniqueThreadSubjectAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueThreadSubjectAccess = new _UniqueThreadSubjectAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueThreadSubjectAccess(condition: Condition) extends UniqueThreadSubjectAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return distinct thread subjects
  * @author Mikko Hilpinen
  * @since 17.10.2023, v0.1
  */
trait UniqueThreadSubjectAccess 
	extends UniqueSubjectAccessLike[ThreadSubject] with SingleRowModelAccess[ThreadSubject] 
		with FilterableView[UniqueThreadSubjectAccess]
{
	// COMPUTED	--------------------
	
	/**
	  * 
		Id of the thread where the referenced subject was used. None if no message thread subject link (or value)
	  *  was found.
	  */
	def threadLinkThreadId(implicit connection: Connection) = pullColumn(threadLinkModel.threadIdColumn).int
	
	/**
	  * 
		Id of the subject used in the specified thread. None if no message thread subject link (or value) was found.
	  */
	def threadLinkSubjectId(implicit connection: Connection) = pullColumn(threadLinkModel.subjectIdColumn).int
	
	/**
	  * Time when this subject was first used in the specified thread. None if no message
	  *  thread subject link (or value) was found.
	  */
	def threadLinkCreated(implicit connection: Connection) = pullColumn(threadLinkModel.createdColumn).instant
	
	/**
	  * A database model (factory) used for interacting with the linked thread link
	  */
	protected def threadLinkModel = MessageThreadSubjectLinkModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = ThreadSubjectFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueThreadSubjectAccess = 
		new UniqueThreadSubjectAccess._UniqueThreadSubjectAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the creation times of the targeted message thread subject links
	  * @param newCreated A new created to assign
	  * @return Whether any message thread subject link was affected
	  */
	def threadLinkCreated_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(threadLinkModel.createdColumn, newCreated)
	
	/**
	  * Updates the subject ids of the targeted message thread subject links
	  * @param newSubjectId A new subject id to assign
	  * @return Whether any message thread subject link was affected
	  */
	def threadLinkSubjectId_=(newSubjectId: Int)(implicit connection: Connection) = 
		putColumn(threadLinkModel.subjectIdColumn, newSubjectId)
	
	/**
	  * Updates the thread ids of the targeted message thread subject links
	  * @param newThreadId A new thread id to assign
	  * @return Whether any message thread subject link was affected
	  */
	def threadLinkThreadId_=(newThreadId: Int)(implicit connection: Connection) = 
		putColumn(threadLinkModel.threadIdColumn, newThreadId)
}

