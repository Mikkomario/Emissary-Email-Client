package vf.emissary.database.access.many.text.word_placement

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.text.WordPlacementFactory
import vf.emissary.database.model.text.WordPlacementModel
import vf.emissary.model.stored.text.WordPlacement

object ManyWordPlacementsAccess
{
	// NESTED	--------------------
	
	private class ManyWordPlacementsSubView(condition: Condition) extends ManyWordPlacementsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple word placements at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait ManyWordPlacementsAccess 
	extends ManyRowModelAccess[WordPlacement] with FilterableView[ManyWordPlacementsAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * statement ids of the accessible word placements
	  */
	def statementIds(implicit connection: Connection) = pullColumn(model.statementIdColumn)
		.map { v => v.getInt }
	
	/**
	  * word ids of the accessible word placements
	  */
	def wordIds(implicit connection: Connection) = pullColumn(model.wordIdColumn).map { v => v.getInt }
	
	/**
	  * order indexs of the accessible word placements
	  */
	def orderIndices(implicit connection: Connection) = pullColumn(model.orderIndexColumn)
		.map { v => v.getInt }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = WordPlacementModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = WordPlacementFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyWordPlacementsAccess = 
		new ManyWordPlacementsAccess.ManyWordPlacementsSubView(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	 * @param wordId Id of the linked word
	 * @return Access to placements of that word
	 */
	def ofWord(wordId: Int) = filter(model.withWordId(wordId).toCondition)
	/**
	 * @param index Targeted position / order index
	 * @return Access to placements at that index
	 */
	def atPosition(index: Int) = filter(model.withOrderIndex(index).toCondition)
	/**
	 * @param wordId Linked word
	 * @param position Targeted placement / position index
	 * @return Access to that word's placement at that location
	 */
	def ofWordAtPosition(wordId: Int, position: Int) =
		filter(model.withWordId(wordId).withOrderIndex(position).toCondition)
	
	/**
	 * @param statementIds Ids of the targeted statements
	 * @return Access to word placement within those statements
	 */
	def inStatements(statementIds: Iterable[Int]) =
		filter(model.statementIdColumn.in(statementIds))
	
	/**
	  * Updates the order indexs of the targeted word placements
	  * @param newOrderIndex A new order index to assign
	  * @return Whether any word placement was affected
	  */
	def orderIndices_=(newOrderIndex: Int)(implicit connection: Connection) =
		putColumn(model.orderIndexColumn, newOrderIndex)
	
	/**
	  * Updates the statement ids of the targeted word placements
	  * @param newStatementId A new statement id to assign
	  * @return Whether any word placement was affected
	  */
	def statementIds_=(newStatementId: Int)(implicit connection: Connection) = 
		putColumn(model.statementIdColumn, newStatementId)
	
	/**
	  * Updates the word ids of the targeted word placements
	  * @param newWordId A new word id to assign
	  * @return Whether any word placement was affected
	  */
	def wordIds_=(newWordId: Int)(implicit connection: Connection) = putColumn(model.wordIdColumn, newWordId)
}

