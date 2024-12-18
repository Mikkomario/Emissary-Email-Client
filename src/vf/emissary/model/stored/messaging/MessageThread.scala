package vf.emissary.model.stored.messaging

import utopia.flow.generic.model.template.ModelLike.AnyModel
import utopia.vault.model.template.{FromIdFactory, StoredFromModelFactory, StoredModelConvertible}
import vf.emissary.database.access.single.messaging.thread.DbSingleMessageThread
import vf.emissary.model.factory.messaging.MessageThreadFactoryWrapper
import vf.emissary.model.partial.messaging.MessageThreadData

object MessageThread extends StoredFromModelFactory[MessageThreadData, MessageThread]
{
	// IMPLEMENTED	--------------------
	
	override def dataFactory = MessageThreadData
	
	override protected def complete(model: AnyModel, data: MessageThreadData) = 
		model("id").tryInt.map { apply(_, data) }
}

/**
  * Represents a message thread that has already been stored in the database
  * @param id id of this message thread in the database
  * @param data Wrapped message thread data
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class MessageThread(id: Int, data: MessageThreadData) 
	extends StoredModelConvertible[MessageThreadData] with FromIdFactory[Int, MessageThread] 
		with MessageThreadFactoryWrapper[MessageThreadData, MessageThread]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this message thread in the database
	  */
	def access = DbSingleMessageThread(id)
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory = data
	
	override def withId(id: Int) = copy(id = id)
	
	override protected def wrap(data: MessageThreadData) = copy(data = data)
}

