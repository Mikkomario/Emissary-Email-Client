package vf.emissary.model.stored.messaging

import utopia.vault.model.template.StoredModelConvertible
import vf.emissary.database.access.single.messaging.subject.DbSingleSubject
import vf.emissary.model.partial.messaging.SubjectData

/**
  * Represents a subject that has already been stored in the database
  * @param id id of this subject in the database
  * @param data Wrapped subject data
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class Subject(id: Int, data: SubjectData) extends StoredModelConvertible[SubjectData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this subject in the database
	  */
	def access = DbSingleSubject(id)
}

