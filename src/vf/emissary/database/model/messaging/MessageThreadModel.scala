package vf.emissary.database.model.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.emissary.database.factory.messaging.MessageThreadFactory
import vf.emissary.model.partial.messaging.MessageThreadData
import vf.emissary.model.stored.messaging.MessageThread

import java.time.Instant

/**
  * Used for constructing MessageThreadModel instances and for inserting message threads to the database
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object MessageThreadModel extends DataInserter[MessageThreadModel, MessageThread, MessageThreadData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains message thread created
	  */
	val createdAttName = "created"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains message thread created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = MessageThreadFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: MessageThreadData) = apply(None, Some(data.created))
	
	override protected def complete(id: Value, data: MessageThreadData) = MessageThread(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param created Time when this thread was opened
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param id A message thread id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
}

/**
  * Used for interacting with MessageThreads in the database
  * @param id message thread database id
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class MessageThreadModel(id: Option[Int] = None, created: Option[Instant] = None)
	extends StorableWithFactory[MessageThread]
{
	// IMPLEMENTED	--------------------
	
	override def factory = MessageThreadModel.factory
	
	override def valueProperties = {
		import MessageThreadModel._
		Vector("id" -> id, createdAttName -> created)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param created Time when this thread was opened
	  * @return A new copy of this model with the specified created
	  */
	def withCreated(created: Instant) = copy(created = Some(created))
}

