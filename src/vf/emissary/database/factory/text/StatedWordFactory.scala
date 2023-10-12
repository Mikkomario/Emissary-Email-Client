package vf.emissary.database.factory.text

import utopia.vault.nosql.factory.row.linked.CombiningFactory
import vf.emissary.model.combined.text.StatedWord
import vf.emissary.model.stored.text.{Word, WordPlacement}

/**
  * Used for reading stated words from the database
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object StatedWordFactory extends CombiningFactory[StatedWord, Word, WordPlacement]
{
	// IMPLEMENTED	--------------------
	
	override def childFactory = WordPlacementFactory
	
	override def parentFactory = WordFactory
	
	override def apply(word: Word, useCase: WordPlacement) = StatedWord(word, useCase)
}

