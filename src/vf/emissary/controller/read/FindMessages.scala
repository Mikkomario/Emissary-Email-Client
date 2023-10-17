package vf.emissary.controller.read

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.Pair
import utopia.flow.operator.Identity
import utopia.flow.operator.EqualsExtensions._
import utopia.flow.util.NotEmpty
import utopia.flow.util.StringExtensions._
import utopia.flow.view.immutable.caching.Lazy
import utopia.vault.database.Connection
import vf.emissary.database.access.many.messaging.address.DbAddresses
import vf.emissary.database.access.many.messaging.address_name.DbAddressNames
import vf.emissary.database.access.many.messaging.message.DbMessages
import vf.emissary.database.access.many.messaging.message_thread_subject_link.DbMessageThreadSubjectLinks
import vf.emissary.database.access.many.messaging.subject_statement_link.DbSubjectStatementLinks
import vf.emissary.database.access.many.text.word.DbWords
import vf.emissary.database.access.many.text.word_placement.DbWordPlacements
import vf.emissary.model.stored.messaging.Message

/**
 * An interface used for finding message data
 * @author Mikko Hilpinen
 * @since 15.10.2023, v0.1
 */
object FindMessages
{
	def apply(addresses: Seq[String], requiredWords: Set[String], preferredWords: Set[String])
	         (implicit connection: Connection) =
	{
		// Finds the applicable senders
		// Prio 1, prio 2, prio 3; None if no prioritization by address shall be used
		lazy val addressIdFilters = NotEmpty(addresses).map { senders =>
			val names = DbAddressNames.like(senders).pull
			val readAddresses = DbAddresses.like(senders).pull
			val potentialMatches = (names.map { sn => sn.addressId -> sn.name } ++
				readAddresses.map { a => a.id -> a.address }).asMultiMap
			
			// Prefers exact matches, as well as matches to multiple specified sender filters
			val (inexactMatches, exactMatches) = potentialMatches
				.divideBy { case (_, names) => names.exists { name => senders.exists { _ ~== name } } }.toTuple
			val (singleWordMatches, multiWordMatches) = inexactMatches
				.divideBy { case (_, names) =>
					names.exists { name => senders.existsCount(2) { _.containsIgnoreCase(name) } }
				}
				.map { _.keySet }.toTuple
			
			(exactMatches.keySet, multiWordMatches, singleWordMatches)
		}
		lazy val allValidAddressIds = addressIdFilters.map { case (p1, p2, p3) => p1 ++ p2 ++ p3 }
		
		// Finds preferred word ids (lazily); Used in prioritization
		// First set is more preferred than the second; Both contain word ids
		val lazyPreferredWordIds = {
			if (preferredWords.nonEmpty) {
				Lazy {
					// Matching is case-insensitive
					val lowerCasePreferred = preferredWords.map { _.toLowerCase }
					DbWords.like(preferredWords.toSeq).pull
						// Separates into exact matches (first) and inexact matches (second)
						.divideBy { w => !lowerCasePreferred.contains(w.text.toLowerCase) }
						.map { _.map { _.id } }
				}
			}
			else
				Lazy.initialized(Pair.twice(Vector.empty))
		}
		
		// Finds places where the specified words are mentioned
		if (requiredWords.nonEmpty) {
			val matchingWords = DbWords.like(requiredWords.toSeq).pull
			if (matchingWords.nonEmpty) {
				val statementIds = DbWordPlacements.ofWords(matchingWords.map { _.id }).statementIds.toSet
				
				// Finds subjects where those words are used
				val subjectIds = DbSubjectStatementLinks.withStatements(statementIds).subjectIds.toSet
				
				// Finds message threads where the specified subjects are used
				val subjectResultMessages = {
					if (subjectIds.nonEmpty) {
						val subjectThreadIds = DbMessageThreadSubjectLinks.usingSubjects(subjectIds).threadIds.toSet
						
						// Limits to threads that involve at least one of the specified addresses, if applicable
						val baseMessageAccess = DbMessages.inThreads(subjectThreadIds)
						allValidAddressIds match {
							case Some(addressIds) => baseMessageAccess.findInvolvingAddresses(addressIds)
							case None => baseMessageAccess.pull
						}
					}
					else
						Vector()
				}
				
				// Finds messages where those statements are used - limits to specific senders
				// TODO: Filter out messages already specified in the first group
				val secondaryResults = Lazy {
					val statingMessages = allValidAddressIds match {
						case Some(addressIds) =>
							DbMessages.findMakingStatementsAndInvolvingAddresses(statementIds, addressIds)
						case None => DbMessages.findMakingStatements(statementIds)
					}
					// TODO: Process
				}
				
				// TODO: Process
			}
		}
	}
	
	private def finalizeMessages(messages: Vector[Message], addressPriorityGroups: Vector[Set[Int]],
	                             wordPriorityGroups: Vector[Set[Int]]) =
	{
		// Finds complete data for each message thread
		
	}
}
