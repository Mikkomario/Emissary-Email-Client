package vf.emissary.database.access.single.text.delimiter

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.stored.text.Delimiter

/**
  * An access point to individual delimiters, based on their id
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class DbSingleDelimiter(id: Int) extends UniqueDelimiterAccess with SingleIntIdModelAccess[Delimiter]

