package vf.emissary.database.access.many.text.word

import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.sql.Condition
import vf.emissary.database.factory.text.WordFactory
import vf.emissary.model.stored.text.Word

object ManyWordsAccess
{
	// NESTED	--------------------
	
	private class ManyWordsSubView(condition: Condition) extends ManyWordsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple words at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait ManyWordsAccess extends ManyWordsAccessLike[Word, ManyWordsAccess] with ManyRowModelAccess[Word]
{
	// IMPLEMENTED	--------------------
	
	override def factory = WordFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyWordsAccess = 
		new ManyWordsAccess.ManyWordsSubView(mergeCondition(filterCondition))
}

