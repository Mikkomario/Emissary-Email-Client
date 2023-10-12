package vf.emissary.database.model.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.emissary.database.factory.messaging.SubjectFactory
import vf.emissary.model.partial.messaging.SubjectData
import vf.emissary.model.stored.messaging.Subject

import java.time.Instant

/**
  * Used for constructing SubjectModel instances and for inserting subjects to the database
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object SubjectModel extends DataInserter[SubjectModel, Subject, SubjectData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains subject author id
	  */
	val authorIdAttName = "authorId"
	
	/**
	  * Name of the property that contains subject created
	  */
	val createdAttName = "created"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains subject author id
	  */
	def authorIdColumn = table(authorIdAttName)
	
	/**
	  * Column that contains subject created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = SubjectFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: SubjectData) = apply(None, Some(data.authorId), Some(data.created))
	
	override protected def complete(id: Value, data: SubjectData) = Subject(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param authorId Id of the address / entity that first used this subject
	  * @return A model containing only the specified author id
	  */
	def withAuthorId(authorId: Int) = apply(authorId = Some(authorId))
	
	/**
	  * @param created Time when this subject was first used
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param id A subject id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
}

/**
  * Used for interacting with Subjects in the database
  * @param id subject database id
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class SubjectModel(id: Option[Int] = None, authorId: Option[Int] = None, 
	created: Option[Instant] = None) 
	extends StorableWithFactory[Subject]
{
	// IMPLEMENTED	--------------------
	
	override def factory = SubjectModel.factory
	
	override def valueProperties = {
		import SubjectModel._
		Vector("id" -> id, authorIdAttName -> authorId, createdAttName -> created)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param authorId Id of the address / entity that first used this subject
	  * @return A new copy of this model with the specified author id
	  */
	def withAuthorId(authorId: Int) = copy(authorId = Some(authorId))
	
	/**
	  * @param created Time when this subject was first used
	  * @return A new copy of this model with the specified created
	  */
	def withCreated(created: Instant) = copy(created = Some(created))
}

