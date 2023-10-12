package vf.emissary.database.access.single.text.word_placement

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.stored.text.WordPlacement

/**
  * An access point to individual word placements, based on their id
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class DbSingleWordPlacement(id: Int) 
	extends UniqueWordPlacementAccess with SingleIntIdModelAccess[WordPlacement]

