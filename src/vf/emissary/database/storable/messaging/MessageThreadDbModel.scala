package vf.emissary.database.storable.messaging

import utopia.flow.collection.immutable.Pair
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.HasIdProperty
import utopia.vault.store.{FromIdFactory, HasId}
import utopia.vault.nosql.storable.StorableFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.factory.messaging.MessageThreadFactory
import vf.emissary.model.partial.messaging.MessageThreadData
import vf.emissary.model.stored.messaging.MessageThread

import java.time.Instant

/**
  * Used for constructing MessageThreadDbModel instances and for inserting message threads to the database
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object MessageThreadDbModel 
	extends StorableFactory[MessageThreadDbModel, MessageThread, MessageThreadData] 
		with FromIdFactory[Int, MessageThreadDbModel] with HasIdProperty 
		with MessageThreadFactory[MessageThreadDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with creation times
	  */
	lazy val created = property("created")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = EmissaryTables.messageThread
	
	override def apply(data: MessageThreadData): MessageThreadDbModel = apply(None, Some(data.created))
	
	/**
	  * @param created Time when this thread was opened
	  * @return A model containing only the specified created
	  */
	override def withCreated(created: Instant) = apply(created = Some(created))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	override protected def complete(id: Value, data: MessageThreadData) = MessageThread(id.getInt, data)
}

/**
  * Used for interacting with MessageThreads in the database
  * @param id message thread database id
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
case class MessageThreadDbModel(id: Option[Int] = None, created: Option[Instant] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, MessageThreadDbModel] 
		with MessageThreadFactory[MessageThreadDbModel]
{
	// IMPLEMENTED	--------------------
	
	override def table = MessageThreadDbModel.table
	
	override def valueProperties = 
		Pair(MessageThreadDbModel.id.name -> id, MessageThreadDbModel.created.name -> created)
	
	/**
	  * @param created Time when this thread was opened
	  * @return A new copy of this model with the specified created
	  */
	override def withCreated(created: Instant) = copy(created = Some(created))
	
	override def withId(id: Int) = copy(id = Some(id))
}

