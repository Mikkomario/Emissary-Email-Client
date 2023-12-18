package vf.emissary.controller.read

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.collection.immutable.Pair
import utopia.flow.operator.equality.EqualsExtensions._
import utopia.flow.util.NotEmpty
import utopia.flow.util.StringExtensions._
import utopia.flow.view.immutable.caching.Lazy
import utopia.vault.database.Connection
import vf.emissary.database.access.many.messaging.address.DbAddresses
import vf.emissary.database.access.many.messaging.address_name.DbAddressNames
import vf.emissary.database.access.many.messaging.message.DbMessages
import vf.emissary.database.access.many.messaging.message_thread.DbMessageThreads
import vf.emissary.database.access.many.messaging.message_thread_subject_link.DbMessageThreadSubjectLinks
import vf.emissary.database.access.many.messaging.subject_statement_link.DbSubjectStatementLinks
import vf.emissary.database.access.many.text.word.DbWords
import vf.emissary.database.access.many.text.word_placement.DbWordPlacements
import vf.emissary.model.combined.messaging.DetailedMessageThread
import vf.emissary.model.enumeration.RecipientType

import scala.annotation.tailrec

/**
 * An interface used for finding message data
 * @author Mikko Hilpinen
 * @since 15.10.2023, v0.1
 */
object FindMessages
{
	/**
	 * Finds all message threads involving the specified addresses (if applicable)
	 * and including the specified words (if applicable).
	 * The results are sorted based on match strength and last message time.
	 * @param addresses Addresses where at least one must be involved in the thread in order for it to be included.
	 *                  Addresses may include names and may be partial.
	 *                  If empty, no address-based filtering is applied.
	 * @param requiredWords Words that must appear in the returned message threads (case-insensitive).
	 *                      May be complete words or word parts.
	 *                      If empty, no word-based filtering is applied.
	 * @param preferredWords Words that are used when prioritizing between different message threads.
	 *                       May be empty and may contain partial words.
	 * @param connection Implicit database connection.
	 *                   Should be kept open during the whole return value iteration,
	 *                   as some computations may be completed lazily.
	 * @throws IllegalArgumentException If both addresses and requiredWords are empty
	 * @return An ordered iterator that returns message threads that fulfill the specified search conditions.
	 *         The best matches are returned first.
	 */
	@throws[IllegalArgumentException]("If both addresses and requiredWords are empty")
	def apply(addresses: Set[String], requiredWords: Set[String], preferredWords: Set[String])
	         (implicit connection: Connection) =
	{
		// Finds the applicable senders
		// Prio 1, prio 2, prio 3; None if no prioritization by address shall be used
		lazy val addressIdFilters = NotEmpty(addresses).map { addresses =>
			val addressSeq = addresses.toSeq
			val names = DbAddressNames.like(addressSeq).pull
			val readAddresses = DbAddresses.like(addressSeq).pull
			val potentialMatches = (names.map { sn => sn.addressId -> sn.name } ++
				readAddresses.map { a => a.id -> a.address }).asMultiMap
			
			// Prefers exact matches, as well as matches to multiple specified sender filters
			val (inexactMatches, exactMatches) = potentialMatches
				.divideBy { case (_, names) => names.exists { name => addresses.exists { _ ~== name } } }.toTuple
			val (singleWordMatches, multiWordMatches) = inexactMatches
				.divideBy { case (_, names) =>
					names.exists { name => addresses.existsCount(2) { _.containsIgnoreCase(name) } }
				}
				.map { _.keySet }.toTuple
			
			Vector(exactMatches.keySet, multiWordMatches, singleWordMatches)
		}
		lazy val allValidAddressIds = addressIdFilters.map { _.flatten.toSet }
		lazy val addressIdPriorityGroups = addressIdFilters.getOrElse(Vector.empty)
		
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
						.map { _.view.map { _.id }.toSet }
				}
			}
			else
				Lazy.initialized(Pair.twice(Set.empty[Int]))
		}
		
		// Mode 1: Finds places where the specified words are mentioned
		if (requiredWords.nonEmpty) {
			val matchingWords = DbWords.like(requiredWords.toSeq).pull
			if (matchingWords.nonEmpty) {
				val statementIds = DbWordPlacements.ofWords(matchingWords.map { _.id }).statementIds.toSet
				
				// Finds subjects where those words are used
				val subjectIds = DbSubjectStatementLinks.withStatements(statementIds).subjectIds.toSet
				// Finds message threads where the specified subjects are used
				val subjectResultThreadIds = {
					if (subjectIds.nonEmpty) {
						val subjectThreadIds = DbMessageThreadSubjectLinks.usingSubjects(subjectIds).threadIds.toSet
						
						// Limits to threads that involve at least one of the specified addresses, if applicable
						allValidAddressIds match {
							case Some(addressIds) =>
								DbMessages.inThreads(subjectThreadIds).findThreadIdsInvolvingAddresses(addressIds)
							case None => subjectThreadIds
						}
					}
					else
						Set[Int]()
				}
				
				// Finds messages where those statements are used - limits to specific addresses
				// This search is performed lazily and provided as a secondary results set
				val secondaryResults = Lazy {
					// Excludes results included in the first group
					val baseAccess = DbMessages.outsideThreads(subjectResultThreadIds)
					allValidAddressIds match {
						case Some(addressIds) =>
							baseAccess.findThreadIdsMakingStatementsAndInvolvingAddresses(statementIds, addressIds)
						case None => baseAccess.findThreadIdsMakingStatements(statementIds)
					}
				}
				
				// Returns all result threads as an ordered iterator
				finalizeMessages(subjectResultThreadIds, addressIdPriorityGroups, lazyPreferredWordIds.value) ++
					secondaryResults.valueIterator.flatMap {
						finalizeMessages(_, addressIdPriorityGroups, lazyPreferredWordIds.value) }
			}
			// Case: No matching words => No results
			else
				Iterator.empty
		}
		else
			allValidAddressIds match {
				case Some(addressIds) =>
					// Finds all thread ids involving the specified addresses
					val threadIds = DbMessages.findThreadIdsInvolvingAddresses(addressIds)
					// Reads and orders the message threads
					finalizeMessages(threadIds, addressIdPriorityGroups, lazyPreferredWordIds.value)
				case None => throw new IllegalArgumentException("Either required addresses or words must be specified")
			}
	}
	
	// Reads thread data and sorts them according to specified priority settings
	private def finalizeMessages(threadIds: Set[Int], addressPriorityGroups: => IndexedSeq[Set[Int]],
	                             wordPriorityGroups: => IndexedSeq[Set[Int]])
	                            (implicit connection: Connection) =
	{
		if (threadIds.nonEmpty) {
			// Finds complete data for each message thread
			val threads = DbMessageThreads(threadIds).pullDetailed
			// Processes the priority groups into non-empty sets
			val validAddressPrioGroups = addressPriorityGroups.filter { _.nonEmpty }
			val validWordPrioGroups = wordPriorityGroups.filter { _.nonEmpty }
			
			// Groups by address priority groups first, if applicable
			val addressGroupsIterator = {
				if (validAddressPrioGroups.nonEmpty) {
					val (primaryAddressGroups, moreAddressGroupsIterator, lazyNonMatchingThreads) =
						groupByAddressPriorityGroups(threads, validAddressPrioGroups)
					(primaryAddressGroups.iterator ++ moreAddressGroupsIterator) :+ lazyNonMatchingThreads.value
				}
				else
					Iterator.single(threads)
			}
			// Secondarily groups by words, if applicable
			val finalGroupsIterator = {
				if (validWordPrioGroups.nonEmpty)
					addressGroupsIterator.flatMap { group =>
						val (primaryWordGroups, moreWordGroupsIterator, lazyNonMatchingThreads) =
							groupByWordPriorityGroups(group, validWordPrioGroups)
						(primaryWordGroups.iterator ++ moreWordGroupsIterator) :+ lazyNonMatchingThreads.value
					}
				else
					addressGroupsIterator
			}
			// Each group is properly sorted by message send time
			// The resulting groups are then lazily flattened into a single sequence of threads
			finalGroupsIterator.flatMap { _.reverseSortBy { _.lastMessageSendTime } }
		}
		else
			Iterator.empty
	}
	
	// Assumes non-empty values (see word-based method comments)
	private def groupByAddressPriorityGroups(threads: Vector[DetailedMessageThread],
	                                         addressPriorityGroups: IndexedSeq[Set[Int]]) =
	{
		// Prefers match as sender
		// Prefers primary recipients to copies to hidden copies
		groupByPriorityGroups(threads, addressPriorityGroups,
			{ (t: DetailedMessageThread, id: Int) => t.involvesAddressAsSender(id) } +:
				RecipientType.values.map { rType => { (thread: DetailedMessageThread, addressId: Int) =>
					thread.involvesAddressAsRecipient(addressId, rType) } },
			0
		)
	}
	
	// Recursively processes threads into word-based priority-groups
	// See groupByPriorityGroups for assumptions and return values
	private def groupByWordPriorityGroups(threads: Vector[DetailedMessageThread],
	                                      wordPriorityGroups: IndexedSeq[Set[Int]]) =
	{
		// Prefers subject-matches to message content -matches
		groupByPriorityGroups(threads, wordPriorityGroups,
			Vector(_.containsWordInSubjects(_), _.containsWordInMessages(_)), 0)
	}
	
	// Recursively processes threads into priority-groups
	// Assumes that threads is non-empty
	// Assumes that each priority-group is non-empty
	// Assumes that the next priority index is within bounds
	// Assumes that filterConditions is non-empty
	// Returns 3 values:
	//      1) Top priority threads, in ordered sub-groups (threads within sub-groups are not ordered)
	//      2) An iterator that returns the remaining threads that matched at least one target,
	//         in similar groups as in value 1
	//      3) Lazy container that yields the threads that were not associated with any of the specified targets
	private def groupByPriorityGroups(threads: Vector[DetailedMessageThread], priorityGroups: IndexedSeq[Set[Int]],
	                                  filterConditions: Vector[(DetailedMessageThread, Int) => Boolean],
	                                  nextPriorityIndex: Int):
	(Vector[Vector[DetailedMessageThread]], Iterator[Vector[DetailedMessageThread]], Lazy[Vector[DetailedMessageThread]]) =
	{
		val targetIds = priorityGroups(nextPriorityIndex)
		// Groups and sorts the threads based on the specified conditions
		// Matches are those threads included in this target id set, properly sorted
		// Non-matches are the threads that are not part of this set, but which may be part of a further set
		val (nonMatches, matches) = filterThreadsBy(Vector(threads -> false), targetIds, filterConditions,
			0)
		
		// Recursively moves to the next group, if appropriate
		// Case: All threads have been processed already or all target groups have been exhausted =>
		//       Returns the remaining threads as the final non-matching group
		if (nonMatches.isEmpty || nextPriorityIndex >= priorityGroups.size - 1)
			(matches, Iterator.empty, Lazy.initialized(nonMatches))
		// Case: Some threads may still match a further set of target ids =>
		//       Recursively moves to the next target id set with the remaining threads
		else {
			// The next iteration is called lazily
			val lazyNextIteration = Lazy {
				groupByPriorityGroups(nonMatches, priorityGroups, filterConditions, nextPriorityIndex + 1)
			}
			// Non-primary target groups are returned as an iterator in order to support lazy calculation
			val remainingIterator = lazyNextIteration.valueIterator.flatMap { _._2 }
			// The final non-matching group is also returned lazily,
			// because it requires all iterations to complete first
			val lazyFinalThreads = Lazy { lazyNextIteration.value._3.value }
			
			(matches, remainingIterator, lazyFinalThreads)
		}
	}
	
	// Expects pre-grouped content, where the second parameter indicates whether the group should be considered a match
	// in any of the previously used filters
	// Assumes that nextConditionIndex is within bounds
	// Assumes that targetIds is non-empty
	// Assumes that threadGroups is non-empty
	// Returns two groups of threads:
	//      1) Those that didn't match any of the conditions and
	//      2) those that matched at least one condition
	// The second group is divided into ordered sub-groups
	// The sub-groups themselves have not been sorted at this stage
	@tailrec
	private def filterThreadsBy(threadGroups: Vector[(Vector[DetailedMessageThread], Boolean)], targetIds: Set[Int],
	                            filterConditions: Vector[(DetailedMessageThread, Int) => Boolean],
	                            nextConditionIndex: Int): (Vector[DetailedMessageThread], Vector[Vector[DetailedMessageThread]]) =
	{
		// Applies the next filter condition in the list
		val condition = filterConditions(nextConditionIndex)
		// Divides into sub-groups based on this condition
		val dividedGroups = threadGroups.flatMap { case (group, hasMatches) =>
			val subGroups = group.groupBy { t => targetIds.count { condition(t, _) } }.toVector.reverseSortBy { _._1 }
			// Notes whether there were any matches to this or a previous condition
			subGroups.map { case (count, group) => group -> (hasMatches || count > 0) }
		}
		
		// Case: More conditions may be applied => Recursively moves to the next condition, dividing the groups further
		if (nextConditionIndex < filterConditions.size - 1)
			filterThreadsBy(dividedGroups, targetIds, filterConditions, nextConditionIndex + 1)
		// Case: No more conditions may be applied => Categorizes the results
		else {
			// Divides into matches (second) and non-matches (first)
			val (nonMatches, matches) = dividedGroups.divideBy { _._2 }.map { _.map { _._1 } }.toTuple
			nonMatches.flatten -> matches
		}
	}
}
