package vf.emissary.model.stored.text

import utopia.vault.model.template.StoredModelConvertible
import vf.emissary.model.partial.text.WordPlacementData
import vf.emissary.model.template.StoredPlaced

/**
  * Represents a word placement that has already been stored in the database
  * @param id id of this word placement in the database
  * @param data Wrapped word placement data
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
@deprecated("Moved to Logos", "v1.1")
case class WordPlacement(id: Int, data: WordPlacementData)
	extends StoredModelConvertible[WordPlacementData] with StoredPlaced[WordPlacementData, Int]
