package vf.emissary.model.stored.messaging

import utopia.vault.model.template.StoredModelConvertible
import vf.emissary.database.access.single.messaging.subject_statement_link.DbSingleSubjectStatementLink
import vf.emissary.model.partial.messaging.SubjectStatementLinkData
import vf.emissary.model.template.StoredPlaced

/**
  * Represents a subject statement link that has already been stored in the database
  * @param id id of this subject statement link in the database
  * @param data Wrapped subject statement link data
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class SubjectStatementLink(id: Int, data: SubjectStatementLinkData) 
	extends StoredModelConvertible[SubjectStatementLinkData] with StoredPlaced[SubjectStatementLinkData, Int]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this subject statement link in the database
	  */
	def access = DbSingleSubjectStatementLink(id)
}

