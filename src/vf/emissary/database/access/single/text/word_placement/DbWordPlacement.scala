package vf.emissary.database.access.single.text.word_placement

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.text.WordPlacementFactory
import vf.emissary.database.model.text.WordPlacementModel
import vf.emissary.model.stored.text.WordPlacement

/**
  * Used for accessing individual word placements
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbWordPlacement extends SingleRowModelAccess[WordPlacement] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = WordPlacementModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = WordPlacementFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted word placement
	  * @return An access point to that word placement
	  */
	def apply(id: Int) = DbSingleWordPlacement(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique word placements.
	  * @return An access point to the word placement that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = UniqueWordPlacementAccess(mergeCondition(condition))
}

