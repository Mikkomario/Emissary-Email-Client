package vf.emissary.database.access.many.text.statement

import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.ChronoRowFactoryView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.text.StatementFactory
import vf.emissary.database.model.text.WordPlacementModel
import vf.emissary.model.stored.text.Statement

object ManyStatementsAccess
{
	// NESTED	--------------------
	
	private class ManyStatementsSubView(condition: Condition) extends ManyStatementsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple statements at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait ManyStatementsAccess 
	extends ManyStatementsAccessLike[Statement, ManyStatementsAccess] with ManyRowModelAccess[Statement] 
		with ChronoRowFactoryView[Statement, ManyStatementsAccess]
{
	// COMPUTED ------------------------
	
	/**
	 * @return Model used for interacting with statement-word links
	 */
	protected def wordLinkModel = WordPlacementModel
	
	/**
	 * @param connection Implicit DB Connection
	 * @return Accessible empty statements (i.e. statements without any words)
	 */
	def pullEmpty(implicit connection: Connection) = findNotLinkedTo(wordLinkModel.table)
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = StatementFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyStatementsAccess = 
		new ManyStatementsAccess.ManyStatementsSubView(mergeCondition(filterCondition))
		
	
	// OTHER    ------------------------
	
	/**
	 * @param wordId     Id of the searched word
	 * @param connection Implicit DB Connection
	 * @return Accessible statements that mention the specified word at the specified location
	 */
	def findWithWordAtIndex(wordId: Int, index: Int)(implicit connection: Connection) =
		find(wordLinkModel.withWordId(wordId).withOrderIndex(index).toCondition,
			joins = Vector(wordLinkModel.table))
	/**
	 * @param wordId     Id of the searched word
	 * @param connection Implicit DB Connection
	 * @return Accessible statements that start with the specified word
	 */
	def findStartingWith(wordId: Int)(implicit connection: Connection) =
		findWithWordAtIndex(wordId, 0)
	
	/**
	 * @param length Targeted length (> 0)
	 * @param connection Implicit DB Connection
	 * @return Accessible statements that are shorter than the specified length
	 */
	def findShorterThan(length: Int)(implicit connection: Connection) =
		findNotLinkedTo(wordLinkModel.table, Some(wordLinkModel.withOrderIndex(length - 1).toCondition))
}

