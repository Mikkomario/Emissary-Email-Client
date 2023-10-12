package vf.emissary.database.access.many.text.word

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple stated words at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbStatedWords extends ManyStatedWordsAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted stated words
	  * @return An access point to stated words with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbStatedWordsSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbStatedWordsSubset(targetIds: Set[Int]) extends ManyStatedWordsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

