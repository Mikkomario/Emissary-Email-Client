package vf.emissary.model.combined.messaging

import utopia.flow.view.template.Extender
import vf.emissary.model.partial.text.WordPlacementData
import vf.emissary.model.stored.text.{Word, WordPlacement}

/**
 * Combines word text and placement information
 * @author Mikko Hilpinen
 * @since 15.10.2023, v0.1
 */
case class DetailedWordPlacement(placement: WordPlacement, word: Word) extends Extender[WordPlacementData]
{
	// COMPUTED ------------------------
	
	/**
	 * @return Id of this word placement
	 */
	def id = placement.id
	
	
	// IMPLEMENTED  --------------------
	
	override def wrapped: WordPlacementData = placement.data
	
	override def toString = word.text
}
