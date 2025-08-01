package vf.emissary.model.stored.messaging

import utopia.flow.generic.model.template.ModelLike.AnyModel
import utopia.vault.store.{FromIdFactory, StoredFromModelFactory, StoredModelConvertible}
import vf.emissary.database.access.single.messaging.subject.DbSingleSubject
import vf.emissary.model.factory.messaging.SubjectFactoryWrapper
import vf.emissary.model.partial.messaging.SubjectData

object Subject extends StoredFromModelFactory[SubjectData, Subject]
{
	// IMPLEMENTED	--------------------
	
	override def dataFactory = SubjectData
	
	override protected def complete(model: AnyModel, data: SubjectData) = 
		model("id").tryInt.map { apply(_, data) }
}

/**
  * Represents a subject that has already been stored in the database
  * @param id id of this subject in the database
  * @param data Wrapped subject data
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class Subject(id: Int, data: SubjectData) 
	extends StoredModelConvertible[SubjectData] with FromIdFactory[Int, Subject] 
		with SubjectFactoryWrapper[SubjectData, Subject]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this subject in the database
	  */
	def access = DbSingleSubject(id)
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory = data
	
	override def withId(id: Int) = copy(id = id)
	
	override protected def wrap(data: SubjectData) = copy(data = data)
}

