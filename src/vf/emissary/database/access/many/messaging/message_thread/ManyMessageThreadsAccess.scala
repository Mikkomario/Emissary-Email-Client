package vf.emissary.database.access.many.messaging.message_thread

import utopia.flow.util.NotEmpty
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.ChronoRowFactoryView
import utopia.vault.sql.Condition
import vf.emissary.database.access.many.messaging.address.DbAddresses
import vf.emissary.database.access.many.messaging.attachment.DbAttachments
import vf.emissary.database.access.many.messaging.message.DbMessages
import vf.emissary.database.access.many.messaging.message_recipient_link.DbMessageRecipientLinks
import vf.emissary.database.access.many.messaging.subject.DbSubjects
import vf.emissary.database.access.many.text.delimiter.DbDelimiters
import vf.emissary.database.access.many.text.statement.{DbMessageStatements, DbStatements, DbSubjectStatements}
import vf.emissary.database.access.many.text.word.DbWords
import vf.emissary.database.access.many.text.word_placement.DbWordPlacements
import vf.emissary.database.access.many.url.link.DbLinks
import vf.emissary.database.access.many.url.link_placement.DbLinkPlacements
import vf.emissary.database.factory.messaging.MessageThreadFactory
import vf.emissary.model.combined.messaging._
import vf.emissary.model.combined.url.{DetailedLink, DetailedLinkPlacement}
import vf.emissary.model.stored.messaging.MessageThread

object ManyMessageThreadsAccess
{
	// NESTED	--------------------
	
	private class ManyMessageThreadsSubView(condition: Condition) extends ManyMessageThreadsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple message threads at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait ManyMessageThreadsAccess 
	extends ManyMessageThreadsAccessLike[MessageThread, ManyMessageThreadsAccess] 
		with ManyRowModelAccess[MessageThread] 
		with ChronoRowFactoryView[MessageThread, ManyMessageThreadsAccess]
{
	// COMPUTED    -----------------------
	
	/**
	 * Pulls all accessible message threads.
	 * Includes all information concerning messages and subjects.
	 * @param connection Implicit DB Connection
	 * @return All accessible message threads in fully detailed form
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
			val wordPlacements = DbWordPlacements.inStatements(statementIds).pull
			val wordMap = DbWords(wordPlacements.map { _.wordId }.toSet).toMapBy { _.id }
			val detailedWordPlacementsPerStatementId = wordPlacements
				.map { p => DetailedWordPlacement(p, wordMap(p.wordId)) }
				.groupBy { _.statementId }
			
			// Pulls links involved
			val linkPlacements = DbLinkPlacements.inStatements(statementIds).pull
			val linkMap = NotEmpty(linkPlacements) match {
				case Some(placements) =>
					DbLinks(placements.map { _.linkId }.toSet).pullDetailed.view.map { l => l.id -> l }.toMap
				case None => Map[Int, DetailedLink]()
			}
			val detailedLinkPlacementsPerStatementId = linkPlacements
				.map { p => DetailedLinkPlacement(p, linkMap(p.linkId)) }
				.groupBy { _.statementId }
			
			// Pulls all statements and delimiters involved
			val statements = DbStatements(statementIds).pull
			val delimiterMap = DbDelimiters(statements.view.flatMap { _.delimiterId }.toSet).toMapBy { _.id }
			
			// Pulls all addresses involved
			val recipientLinks = DbMessageRecipientLinks.inMessages(messageIds).pull
			val addressMap = DbAddresses(messages.map { _.senderId }.toSet ++ recipientLinks.map { _.recipientId })
				.pullWithNames
				.view.map { a => a.id -> a }.toMap
			val recipientsPerMessageId = recipientLinks
				.map { link => NamedMessageRecipient(addressMap(link.recipientId), link) }
				.groupBy { _.messageId }
			
			// Pulls all attachments involved
			// Maps to message id
			val attachmentsPerMessageId = DbAttachments.inMessages(messageIds).pull.groupBy { _.messageId }
			
			// Combines the information together
			val detailedStatementMap = statements.view.map { s =>
				s.id -> DetailedStatement(s,
					detailedWordPlacementsPerStatementId.getOrElse(s.id, Vector.empty),
					detailedLinkPlacementsPerStatementId.getOrElse(s.id, Vector.empty),
					s.delimiterId.flatMap(delimiterMap.get)
				)
			}.toMap
			val detailedStatementsPerMessageId = messageStatements
				.map { statement => statement.messageLink -> detailedStatementMap(statement.statement.id) }
				.groupBy { _._1.messageId }
			val detailedMessagesPerThreadId = messages
				.map { m =>
					DetailedMessage(m,
						addressMap(m.senderId), recipientsPerMessageId.getOrElse(m.id, Vector.empty),
						detailedStatementsPerMessageId.getOrElse(m.id, Vector.empty)
							.sortBy { _._1.orderIndex }.map { _._2 },
						attachmentsPerMessageId.getOrElse(m.id, Vector.empty))
				}
				.groupBy { _.threadId }
			val detailedStatementsPerSubjectId = subjectStatements
				.map { s => s.subjectLink -> detailedStatementMap(s.statement.id) }
				.groupBy { _._1.subjectId }
			val detailedSubjectsPerThreadId = subjects
				.map { s =>
					s.threadLink -> DetailedSubject(s.subject,
						detailedStatementsPerSubjectId.getOrElse(s.id, Vector.empty)
							.sortBy { _._1.orderIndex }.map { _._2 })
				}
				.groupBy { _._1.threadId }
			
			threads.map { t =>
				DetailedMessageThread(t,
					detailedSubjectsPerThreadId.getOrElse(t.id, Vector.empty).sortBy { _._1.created }.map { _._2 },
					detailedMessagesPerThreadId.getOrElse(t.id, Vector.empty).sortBy { _.created })
			}
		}
		else
			Vector.empty
	}
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageThreadFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyMessageThreadsAccess = 
		new ManyMessageThreadsAccess.ManyMessageThreadsSubView(mergeCondition(filterCondition))
}

