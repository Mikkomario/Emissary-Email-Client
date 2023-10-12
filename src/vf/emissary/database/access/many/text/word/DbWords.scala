package vf.emissary.database.access.many.text.word

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple words at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbWords extends ManyWordsAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted words
	  * @return An access point to words with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbWordsSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbWordsSubset(targetIds: Set[Int]) extends ManyWordsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

