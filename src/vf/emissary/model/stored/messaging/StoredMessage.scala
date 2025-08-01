package vf.emissary.model.stored.messaging

import utopia.flow.generic.model.template.ModelLike.AnyModel
import utopia.vault.store.{FromIdFactory, StoredFromModelFactory, StoredModelConvertible}
import vf.emissary.database.access.single.messaging.message.DbSingleMessage
import vf.emissary.model.factory.messaging.MessageFactoryWrapper
import vf.emissary.model.partial.messaging.MessageData

object StoredMessage extends StoredFromModelFactory[MessageData, StoredMessage]
{
	// IMPLEMENTED	--------------------
	
	override def dataFactory = MessageData
	
	override protected def complete(model: AnyModel, data: MessageData) = 
		model("id").tryInt.map { apply(_, data) }
}

/**
  * Represents a message that has already been stored in the database
  * @param id id of this message in the database
  * @param data Wrapped message data
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class StoredMessage(id: Int, data: MessageData)
	extends StoredModelConvertible[MessageData] with FromIdFactory[Int, StoredMessage]
		with MessageFactoryWrapper[MessageData, StoredMessage]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this message in the database
	  */
	def access = DbSingleMessage(id)
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory = data
	
	override def withId(id: Int) = copy(id = id)
	
	override protected def wrap(data: MessageData) = copy(data = data)
}

