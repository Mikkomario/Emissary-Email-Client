package vf.emissary.database.access.single.text.statement

import utopia.flow.util.NotEmpty
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.access.many.text.statement.DbStatements
import vf.emissary.database.factory.text.StatementFactory
import vf.emissary.database.model.text.{StatementModel, WordPlacementModel}
import vf.emissary.model.partial.text.{StatementData, WordPlacementData}
import vf.emissary.model.stored.text.Statement

/**
  * Used for accessing individual statements
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbStatement extends SingleRowModelAccess[Statement] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = StatementModel
	
	/**
	 * @return Model used for interacting with statement-word links
	 */
	protected def wordLinkModel = WordPlacementModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = StatementFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted statement
	  * @return An access point to that statement
	  */
	def apply(id: Int) = DbSingleStatement(id)
	
	/**
	 * Stores a statement to the database. Avoids inserting duplicate entries.
	 * @param wordIds Ids of the words that form this statement
	 * @param delimiterId Id of the delimiter that ends this statement.
	 *                    None if this statement doesn't end with a delimiter.
	 * @param connection Implicit DB connection
	 * @return Existing (right) or inserted (left) statement
	 */
	def store(wordIds: Seq[Int], delimiterId: Option[Int])(implicit connection: Connection) = {
		// Case: Empty statement => Pulls or inserts
		if (wordIds.isEmpty)
			DbStatements.endingWith(delimiterId).pullEmpty.headOption
				.toRight { model.insert(StatementData(delimiterId)) }
		// Case: Non-empty statement => Finds potential matches
		else {
			val initialMatchIds = DbStatements.endingWith(delimiterId).findStartingWith(wordIds.head).map { _.id }.toSet
			// Reduces the number of potential matches by including more words
			val remainingMatchIds = wordIds.zipWithIndex.tail
				.foldLeft(initialMatchIds) { case (potentialStatementIds, (wordId, wordIndex)) =>
					if (potentialStatementIds.isEmpty)
						potentialStatementIds
					else {
						DbStatements(potentialStatementIds).findWithWordAtIndex(wordId, wordIndex).map { _.id }.toSet
					}
				}
			NotEmpty(remainingMatchIds)
				// Only accepts statements of specific length
				.flatMap { remaining => DbStatements(remaining).findShorterThan(wordIds.size + 1).headOption }
				// If no such statement exists, inserts it
				.toRight {
					val statement = model.insert(StatementData(delimiterId))
					wordLinkModel.insert(wordIds.zipWithIndex.map { case (wordId, wordIndex) =>
						WordPlacementData(statement.id, wordId, wordIndex)
					})
					statement
				}
		}
	}
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique statements.
	  * @return An access point to the statement that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = UniqueStatementAccess(mergeCondition(condition))
}

