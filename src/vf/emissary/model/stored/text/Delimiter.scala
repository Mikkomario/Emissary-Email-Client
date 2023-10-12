package vf.emissary.model.stored.text

import utopia.vault.model.template.StoredModelConvertible
import vf.emissary.database.access.single.text.delimiter.DbSingleDelimiter
import vf.emissary.model.partial.text.DelimiterData

/**
  * Represents a delimiter that has already been stored in the database
  * @param id id of this delimiter in the database
  * @param data Wrapped delimiter data
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class Delimiter(id: Int, data: DelimiterData) extends StoredModelConvertible[DelimiterData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this delimiter in the database
	  */
	def access = DbSingleDelimiter(id)
}

