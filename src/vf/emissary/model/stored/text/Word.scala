package vf.emissary.model.stored.text

import utopia.vault.model.template.StoredModelConvertible
import vf.emissary.model.partial.text.WordData

/**
  * Represents a word that has already been stored in the database
  * @param id id of this word in the database
  * @param data Wrapped word data
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
@deprecated("Moved to Logos & renamed to StoredWord", "v1.1")
case class Word(id: Int, data: WordData) extends StoredModelConvertible[WordData]
{
	// IMPLEMENTED  ----------------
	
	override def toString = data.text
}

