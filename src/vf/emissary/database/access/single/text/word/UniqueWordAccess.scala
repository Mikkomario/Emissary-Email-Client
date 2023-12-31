package vf.emissary.database.access.single.text.word

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.text.WordFactory
import vf.emissary.model.stored.text.Word

object UniqueWordAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueWordAccess = new _UniqueWordAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueWordAccess(condition: Condition) extends UniqueWordAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct words.
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait UniqueWordAccess 
	extends UniqueWordAccessLike[Word] with SingleRowModelAccess[Word] with FilterableView[UniqueWordAccess]
{
	// IMPLEMENTED	--------------------
	
	override def factory = WordFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueWordAccess = 
		new UniqueWordAccess._UniqueWordAccess(mergeCondition(filterCondition))
}

