package vf.emissary.database.access.single.text.word

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.text.StatedWordFactory
import vf.emissary.database.model.text.{WordModel, WordPlacementModel}
import vf.emissary.model.combined.text.StatedWord

/**
  * Used for accessing individual stated words
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbStatedWord extends SingleRowModelAccess[StatedWord] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * A database model (factory) used for interacting with linked words
	  */
	protected def model = WordModel
	
	/**
	  * A database model (factory) used for interacting with the linked use case
	  */
	protected def useCaseModel = WordPlacementModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = StatedWordFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted stated word
	  * @return An access point to that stated word
	  */
	def apply(id: Int) = DbSingleStatedWord(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique stated words.
	  * @return An access point to the stated word that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = UniqueStatedWordAccess(mergeCondition(condition))
}

