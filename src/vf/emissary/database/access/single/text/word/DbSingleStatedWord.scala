package vf.emissary.database.access.single.text.word

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.combined.text.StatedWord

/**
  * An access point to individual stated words, based on their word id
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class DbSingleStatedWord(id: Int) extends UniqueStatedWordAccess with SingleIntIdModelAccess[StatedWord]

