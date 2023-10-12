package vf.emissary.model.stored.text

import utopia.vault.model.template.StoredModelConvertible
import vf.emissary.database.access.single.text.statement.DbSingleStatement
import vf.emissary.model.partial.text.StatementData

/**
  * Represents a statement that has already been stored in the database
  * @param id id of this statement in the database
  * @param data Wrapped statement data
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class Statement(id: Int, data: StatementData) extends StoredModelConvertible[StatementData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this statement in the database
	  */
	def access = DbSingleStatement(id)
}

