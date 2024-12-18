package vf.emissary.database.access.single.text.statement

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.stored.text.Statement

/**
  * An access point to individual statements, based on their id
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
@deprecated("Moved to Logos", "v1.1")
case class DbSingleStatement(id: Int) extends UniqueStatementAccess with SingleIntIdModelAccess[Statement]

