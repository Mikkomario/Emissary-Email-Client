package vf.emissary.database.access.single.text.word

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.text.StatedWordFactory
import vf.emissary.database.model.text.WordPlacementModel
import vf.emissary.model.combined.text.StatedWord

object UniqueStatedWordAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueStatedWordAccess = new _UniqueStatedWordAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueStatedWordAccess(condition: Condition) extends UniqueStatedWordAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return distinct stated words
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait UniqueStatedWordAccess 
	extends UniqueWordAccessLike[StatedWord] with SingleRowModelAccess[StatedWord] 
		with FilterableView[UniqueStatedWordAccess]
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the statement where the referenced word appears. None if no word placement (or value) was found.
	  */
	def useCaseStatementId(implicit connection: Connection) = pullColumn(useCaseModel.statementIdColumn).int
	
	/**
	  * 
		Id of the word that appears in the described statement. None if no word placement (or value) was found.
	  */
	def useCaseWordId(implicit connection: Connection) = pullColumn(useCaseModel.wordIdColumn).int
	
	/**
	  * Index at which the specified word appears within the referenced statement (0-based). None if
	  *  no word placement (or value) was found.
	  */
	def useCaseOrderIndex(implicit connection: Connection) = pullColumn(useCaseModel.orderIndexColumn).int
	
	/**
	  * A database model (factory) used for interacting with the linked use case
	  */
	protected def useCaseModel = WordPlacementModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = StatedWordFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueStatedWordAccess = 
		new UniqueStatedWordAccess._UniqueStatedWordAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the order indexs of the targeted word placements
	  * @param newOrderIndex A new order index to assign
	  * @return Whether any word placement was affected
	  */
	def useCaseOrderIndex_=(newOrderIndex: Int)(implicit connection: Connection) = 
		putColumn(useCaseModel.orderIndexColumn, newOrderIndex)
	
	/**
	  * Updates the statement ids of the targeted word placements
	  * @param newStatementId A new statement id to assign
	  * @return Whether any word placement was affected
	  */
	def useCaseStatementId_=(newStatementId: Int)(implicit connection: Connection) = 
		putColumn(useCaseModel.statementIdColumn, newStatementId)
	
	/**
	  * Updates the word ids of the targeted word placements
	  * @param newWordId A new word id to assign
	  * @return Whether any word placement was affected
	  */
	def useCaseWordId_=(newWordId: Int)(implicit connection: Connection) = 
		putColumn(useCaseModel.wordIdColumn, newWordId)
}

