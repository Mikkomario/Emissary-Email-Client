package vf.emissary.database.access.many.messaging.thread

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.{Empty, IntSet}
import utopia.logos.database.access.many.text.statement.DbStatements
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.{ChronoRowFactoryView, ViewFactory}
import utopia.vault.sql.Condition
import vf.emissary.database.access.many.messaging.address.DbAddresses
import vf.emissary.database.access.many.messaging.attachment.DbAttachments
import vf.emissary.database.access.many.messaging.message.DbMessages
import vf.emissary.database.access.many.messaging.message.link.recipient.DbMessageRecipientLinks
import vf.emissary.database.access.many.messaging.subject.DbSubjects
import vf.emissary.database.access.many.text.statement.{DbMessageStatements, DbSubjectStatements}
import vf.emissary.database.factory.messaging.MessageThreadDbFactory
import vf.emissary.model.combined.messaging.{DetailedMessage, DetailedMessageThread, DetailedSubject, NamedMessageRecipient}
import vf.emissary.model.stored.messaging.MessageThread

object ManyMessageThreadsAccess extends ViewFactory[ManyMessageThreadsAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyMessageThreadsAccess = 
		_ManyMessageThreadsAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyMessageThreadsAccess(override val accessCondition: Option[Condition]) 
		extends ManyMessageThreadsAccess
}

/**
  * A common trait for access points which target multiple message threads at a time
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait ManyMessageThreadsAccess 
	extends ManyMessageThreadsAccessLike[MessageThread, ManyMessageThreadsAccess] 
		with ManyRowModelAccess[MessageThread] 
		with ChronoRowFactoryView[MessageThread, ManyMessageThreadsAccess]
{
	// COMPUTED	--------------------
	
	/**
	 * Pulls all accessible message threads.
	 * Includes all information concerning messages and subjects.
	 * @param connection Implicit DB Connection
	 */
	def pullDetailed(implicit connection: Connection) = {
		// Pulls standard thread data
		val threads = pull
		if (threads.nonEmpty) {
			// Pulls all subjects involved
			val threadIds = threads.view.map { _.id }.toIntSet
			val subjects = DbSubjects.threadSpecific.inThreads(threadIds).pull
			val subjectIds = subjects.view.map { _.id }.toIntSet
			val subjectStatements = DbSubjectStatements.inSubjects(subjectIds).pull
			
			// Pulls all messages involved
			val messages = DbMessages.inThreads(threadIds).pull
			val messageIds = messages.view.map { _.id }.toIntSet
			val messageStatements = DbMessageStatements.inMessages(messageIds).pull
			
			// Pulls words involved
			val statementMap = DbStatements(subjectStatements.map { _.id } ++ messageStatements.map { _.id })
				.pullDetailed.view.map { s => s.id -> s }.toMap
			
			// Pulls all addresses involved
			val recipientLinks = DbMessageRecipientLinks.inMessages(messageIds).pull
			val addressMap = DbAddresses(IntSet.from(messages.map { _.senderId } ++
				recipientLinks.map { _.recipientId }))
				.pullWithNames
				.view.map { a => a.id -> a }.toMap
			val recipientsPerMessageId = recipientLinks
				.map { link => NamedMessageRecipient(addressMap(link.recipientId), link) }
				.groupBy { _.messageId }.withDefaultValue(Empty)
			
			// Pulls all attachments involved
			// Maps to message id
			val attachmentsPerMessageId = DbAttachments.messageLinked.inMessages(messageIds).pull
				.groupMap { _.link.messageId } { _.attachment }.withDefaultValue(Empty)
			
			// Combines the information together
			val detailedStatementsPerMessageId = messageStatements
				.map { statement => statement.messageLink -> statementMap(statement.statement.id) }
				.groupBy { _._1.messageId }.withDefaultValue(Empty)
			val detailedMessagesPerThreadId = messages
				.map { m =>
					DetailedMessage(m,
						addressMap(m.senderId), recipientsPerMessageId(m.id),
						detailedStatementsPerMessageId(m.id).sortBy { _._1.orderIndex }.map { _._2 },
						attachmentsPerMessageId(m.id))
				}
				.groupBy { _.threadId }.withDefaultValue(Empty)
			val detailedStatementsPerSubjectId = subjectStatements
				.map { s => s.subjectLink -> statementMap(s.statement.id) }
				.groupBy { _._1.subjectId }.withDefaultValue(Empty)
			val detailedSubjectsPerThreadId = subjects
				.map { s =>
					s.threadLink -> DetailedSubject(s.subject,
						detailedStatementsPerSubjectId(s.id).sortBy { _._1.orderIndex }.map { _._2 })
				}
				.groupBy { _._1.threadId }.withDefaultValue(Empty)
			
			threads.map { t =>
				DetailedMessageThread(t,
					detailedSubjectsPerThreadId(t.id).sortBy { _._1.created }.map { _._2 },
					detailedMessagesPerThreadId(t.id).sortBy { _.created })
			}
		}
		else
			Empty
	}
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageThreadDbFactory
	override protected def self = this
	
	override def apply(condition: Condition): ManyMessageThreadsAccess = ManyMessageThreadsAccess(condition)
}

