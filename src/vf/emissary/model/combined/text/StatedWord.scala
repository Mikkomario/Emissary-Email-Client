package vf.emissary.model.combined.text

import utopia.flow.view.template.Extender
import vf.emissary.model.partial.text.WordData
import vf.emissary.model.stored.text.{Word, WordPlacement}

/**
  * Represents a word used in a specific statement
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class StatedWord(word: Word, useCase: WordPlacement) extends Extender[WordData]
{
	// COMPUTED	--------------------
	
	/**
	  * Id of this word in the database
	  */
	def id = word.id
	
	
	// IMPLEMENTED	--------------------
	
	override def wrapped = word.data
}

