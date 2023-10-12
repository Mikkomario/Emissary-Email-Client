package vf.emissary.database.access.single.text.word

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.text.WordFactory
import vf.emissary.database.model.text.WordModel
import vf.emissary.model.stored.text.Word

/**
  * Used for accessing individual words
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbWord extends SingleRowModelAccess[Word] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = WordModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = WordFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted word
	  * @return An access point to that word
	  */
	def apply(id: Int) = DbSingleWord(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique words.
	  * @return An access point to the word that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = UniqueWordAccess(mergeCondition(condition))
}

