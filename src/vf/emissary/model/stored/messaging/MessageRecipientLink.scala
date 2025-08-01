package vf.emissary.model.stored.messaging

import utopia.flow.generic.model.template.ModelLike.AnyModel
import utopia.vault.store.{FromIdFactory, StoredFromModelFactory, StoredModelConvertible}
import vf.emissary.database.access.single.messaging.message.link.recipient.DbSingleMessageRecipientLink
import vf.emissary.model.factory.messaging.MessageRecipientLinkFactoryWrapper
import vf.emissary.model.partial.messaging.MessageRecipientLinkData

object MessageRecipientLink extends StoredFromModelFactory[MessageRecipientLinkData, MessageRecipientLink]
{
	// IMPLEMENTED	--------------------
	
	override def dataFactory = MessageRecipientLinkData
	
	override protected def complete(model: AnyModel, data: MessageRecipientLinkData) = 
		model("id").tryInt.map { apply(_, data) }
}

/**
  * Represents a message recipient link that has already been stored in the database
  * @param id id of this message recipient link in the database
  * @param data Wrapped message recipient link data
  * @author Mikko Hilpinen
  * @since 15.10.2023, v0.1
  */
case class MessageRecipientLink(id: Int, data: MessageRecipientLinkData) 
	extends StoredModelConvertible[MessageRecipientLinkData] with FromIdFactory[Int, MessageRecipientLink] 
		with MessageRecipientLinkFactoryWrapper[MessageRecipientLinkData, MessageRecipientLink]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this message recipient link in the database
	  */
	def access = DbSingleMessageRecipientLink(id)
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory = data
	
	override def withId(id: Int) = copy(id = id)
	
	override protected def wrap(data: MessageRecipientLinkData) = copy(data = data)
}

