package vf.emissary.database.access.single.text.word

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.stored.text.Word

/**
  * An access point to individual words, based on their id
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class DbSingleWord(id: Int) extends UniqueWordAccess with SingleIntIdModelAccess[Word]

