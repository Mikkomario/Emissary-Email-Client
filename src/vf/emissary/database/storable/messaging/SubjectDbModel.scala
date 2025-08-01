package vf.emissary.database.storable.messaging

import utopia.flow.collection.immutable.Pair
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.HasIdProperty
import utopia.vault.store.{FromIdFactory, HasId}
import utopia.vault.nosql.storable.StorableFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.factory.messaging.SubjectFactory
import vf.emissary.model.partial.messaging.SubjectData
import vf.emissary.model.stored.messaging.Subject

import java.time.Instant

/**
  * Used for constructing SubjectDbModel instances and for inserting subjects to the database
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object SubjectDbModel 
	extends StorableFactory[SubjectDbModel, Subject, SubjectData] with FromIdFactory[Int, SubjectDbModel] 
		with HasIdProperty with SubjectFactory[SubjectDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with creation times
	  */
	lazy val created = property("created")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = EmissaryTables.subject
	
	override def apply(data: SubjectData): SubjectDbModel = apply(None, Some(data.created))
	
	/**
	  * @param created Time when this subject was first used
	  * @return A model containing only the specified created
	  */
	override def withCreated(created: Instant) = apply(created = Some(created))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	override protected def complete(id: Value, data: SubjectData) = Subject(id.getInt, data)
}

/**
  * Used for interacting with Subjects in the database
  * @param id subject database id
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
case class SubjectDbModel(id: Option[Int] = None, created: Option[Instant] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, SubjectDbModel] 
		with SubjectFactory[SubjectDbModel]
{
	// IMPLEMENTED	--------------------
	
	override def table = SubjectDbModel.table
	
	override def valueProperties = Pair(SubjectDbModel.id.name -> id, SubjectDbModel.created.name -> created)
	
	/**
	  * @param created Time when this subject was first used
	  * @return A new copy of this model with the specified created
	  */
	override def withCreated(created: Instant) = copy(created = Some(created))
	
	override def withId(id: Int) = copy(id = Some(id))
}

