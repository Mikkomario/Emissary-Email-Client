package vf.emissary.database.access.many.messaging.thread

import utopia.flow.collection.immutable.Empty
import utopia.flow.util.NotEmpty
import utopia.logos.database.access.many.text.delimiter.DbDelimiters
import utopia.logos.database.access.many.text.statement.DbStatements
import utopia.logos.database.access.many.text.word.DbWords
import utopia.logos.database.access.many.text.word.placement.DbWordPlacements
import utopia.logos.database.access.many.url.link.DbLinks
import utopia.logos.database.access.many.url.link.placement.DbLinkPlacements
import utopia.logos.model.combined.text.{DetailedStatement, StatedWord}
import utopia.logos.model.combined.url.{DetailedLink, DetailedLinkPlacement}
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
			val threadIds = threads.map { _.id }
			val subjects = DbSubjects.threadSpecific.inThreads(threadIds).pull
			val subjectIds = subjects.map { _.id }.toSet
			val subjectStatements = DbSubjectStatements.inSubjects(subjectIds).pull
			
			// Pulls all messages involved
			val messages = DbMessages.inThreads(threadIds).pull
			val messageIds = messages.map { _.id }
			val messageStatements = DbMessageStatements.inMessages(messageIds).pull
			
			// Pulls words involved
			val statementIds = subjectStatements.map { _.id }.toSet ++ messageStatements.map { _.id }
			val wordPlacements = DbWordPlacements.withinStatements(statementIds).pull
			val wordMap = DbWords(wordPlacements.map { _.wordId }.toSet).toMapBy { _.id }
			val detailedWordPlacementsPerStatementId = wordPlacements
				.map { p => StatedWord(wordMap(p.wordId), p) }
				.groupBy { _.useCase.statementId }.withDefaultValue(Empty)
			
			// Pulls links involved
			val linkPlacements = DbLinkPlacements.withinStatements(statementIds).pull
			val linkMap = NotEmpty(linkPlacements) match {
				case Some(placements) =>
					DbLinks(placements.map { _.linkId }.toSet).pullDetailed.view.map { l => l.id -> l }.toMap
				case None => Map[Int, DetailedLink]()
			}
			val detailedLinkPlacementsPerStatementId = linkPlacements
				.map { p => DetailedLinkPlacement(p, linkMap(p.linkId)) }
				.groupBy { _.statementId }.withDefaultValue(Empty)
			
			// Pulls all statements and delimiters involved
			val statements = DbStatements(statementIds).pull
			val delimiterMap = DbDelimiters(statements.view.flatMap { _.delimiterId }.toSet).toMapBy { _.id }
			
			// Pulls all addresses involved
			val recipientLinks = DbMessageRecipientLinks.inMessages(messageIds).pull
			val addressMap = DbAddresses(messages.map { _.senderId }.toSet ++
				recipientLinks.map { _.recipientId })
				.pullWithNames
				.view.map { a => a.id -> a }.toMap
			val recipientsPerMessageId = recipientLinks
				.map { link => NamedMessageRecipient(addressMap(link.recipientId), link) }
				.groupBy { _.messageId }.withDefaultValue(Empty)
			
			// Pulls all attachments involved
			// Maps to message id
			val attachmentsPerMessageId = DbAttachments.withinMessages(messageIds).pull
				.groupBy { _.messageId }.withDefaultValue(Empty)
			
			// Combines the information together
			val detailedStatementMap = statements.view.map { s =>
				s.id -> DetailedStatement(s,
					detailedWordPlacementsPerStatementId(s.id),
					detailedLinkPlacementsPerStatementId(s.id),
					s.delimiterId.flatMap(delimiterMap.get)
				)
			}.toMap
			val detailedStatementsPerMessageId = messageStatements
				.map { statement => statement.messageLink -> detailedStatementMap(statement.statement.id) }
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
				.map { s => s.subjectLink -> detailedStatementMap(s.statement.id) }
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

