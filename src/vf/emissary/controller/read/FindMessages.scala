package vf.emissary.controller.read

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.operator.Identity
import utopia.flow.operator.EqualsExtensions._
import utopia.flow.util.NotEmpty
import utopia.flow.util.StringExtensions._
import utopia.vault.database.Connection
import vf.emissary.database.access.many.messaging.address.DbAddresses
import vf.emissary.database.access.many.messaging.address_name.DbAddressNames
import vf.emissary.database.access.many.messaging.subject_statement_link.DbSubjectStatementLinks
import vf.emissary.database.access.many.text.word.DbWords
import vf.emissary.database.access.many.text.word_placement.DbWordPlacements

/**
 * An interface used for finding message data
 * @author Mikko Hilpinen
 * @since 15.10.2023, v0.1
 */
object FindMessages
{
	def apply(senders: Seq[String], words: Seq[String])(implicit connection: Connection) = {
		// Finds the applicable senders
		// Prio 1, prio 2, prio 3; None if no prioritization by sender shall be used
		lazy val senderIdFilters = NotEmpty(senders).map { senders =>
			val senderNames = DbAddressNames.like(senders).pull
			val addresses = DbAddresses.like(senders).pull
			val potentialMatches = (senderNames.map { sn => sn.addressId -> sn.name } ++
				addresses.map { a => a.id -> a.address }).asMultiMap
			
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
		lazy val allValidSenderIds = senderIdFilters.map { case (p1, p2, p3) => p1 ++ p2 ++ p3 }
		
		// Finds places where the specified words are mentioned
		if (words.nonEmpty) {
			val matchingWords = DbWords.like(words).pull
			if (matchingWords.nonEmpty) {
				val wordPlacements = DbWordPlacements.ofWords(matchingWords.map { _.id }).pull
				val wordPlacementsPerStatementId = wordPlacements.groupMap { _.statementId }(Identity)
				
				// Finds subjects where those words are used
				val subjectLinks = DbSubjectStatementLinks.withStatements(wordPlacementsPerStatementId.keys).pull
				val statementLinksPerSubjectId = subjectLinks.groupMap { _.subjectId }(Identity)
				
				// Finds messages where those words are used - limits to specific senders
				
			}
		}
	}
}
