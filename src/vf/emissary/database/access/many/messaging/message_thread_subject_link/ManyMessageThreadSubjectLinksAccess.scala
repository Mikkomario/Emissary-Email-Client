package vf.emissary.database.access.many.messaging.message_thread_subject_link

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.ChronoRowFactoryView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.MessageThreadSubjectLinkFactory
import vf.emissary.database.model.messaging.MessageThreadSubjectLinkModel
import vf.emissary.model.stored.messaging.MessageThreadSubjectLink

import java.time.Instant

object ManyMessageThreadSubjectLinksAccess
{
	// NESTED	--------------------
	
	private class ManyMessageThreadSubjectLinksSubView(condition: Condition) 
		extends ManyMessageThreadSubjectLinksAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple message thread subject links at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait ManyMessageThreadSubjectLinksAccess 
	extends ManyRowModelAccess[MessageThreadSubjectLink] 
		with ChronoRowFactoryView[MessageThreadSubjectLink, ManyMessageThreadSubjectLinksAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * thread ids of the accessible message thread subject links
	  */
	def threadIds(implicit connection: Connection) = pullColumn(model.threadIdColumn).map { v => v.getInt }
	/**
	  * subject ids of the accessible message thread subject links
	  */
	def subjectIds(implicit connection: Connection) = pullColumn(model.subjectIdColumn).map { v => v.getInt }
	
	/**
	  * creation times of the accessible message thread subject links
	  */
	def creationTimes(implicit connection: Connection) = pullColumn(model.createdColumn)
		.map { v => v.getInstant }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = MessageThreadSubjectLinkModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageThreadSubjectLinkFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyMessageThreadSubjectLinksAccess = 
		new ManyMessageThreadSubjectLinksAccess
			.ManyMessageThreadSubjectLinksSubView(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	 * @param messageThreadId Id of the targeted message thread
	 * @return Access to subjects used within that thread
	 */
	def inThread(messageThreadId: Int) =
		filter(model.withThreadId(messageThreadId).toCondition)
	
	/**
	 * @param subjectIds Ids of the targeted message subjects
	 * @return Access to links involving those subjects
	 */
	def usingSubjects(subjectIds: Iterable[Int]) = filter(model.subjectIdColumn.in(subjectIds))
	
	/**
	  * Updates the creation times of the targeted message thread subject links
	  * @param newCreated A new created to assign
	  * @return Whether any message thread subject link was affected
	  */
	def creationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the subject ids of the targeted message thread subject links
	  * @param newSubjectId A new subject id to assign
	  * @return Whether any message thread subject link was affected
	  */
	def subjectIds_=(newSubjectId: Int)(implicit connection: Connection) = 
		putColumn(model.subjectIdColumn, newSubjectId)
	
	/**
	  * Updates the thread ids of the targeted message thread subject links
	  * @param newThreadId A new thread id to assign
	  * @return Whether any message thread subject link was affected
	  */
	def threadIds_=(newThreadId: Int)(implicit connection: Connection) = 
		putColumn(model.threadIdColumn, newThreadId)
}

