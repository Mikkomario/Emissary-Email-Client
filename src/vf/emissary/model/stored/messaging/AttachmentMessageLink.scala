package vf.emissary.model.stored.messaging

import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import utopia.vault.store.{FromIdFactory, StoredFromModelFactory, StoredModelConvertible}
import vf.emissary.database.access.single.messaging.attachment.link.message.DbSingleAttachmentMessageLink
import vf.emissary.model.factory.messaging.AttachmentMessageLinkFactoryWrapper
import vf.emissary.model.partial.messaging.AttachmentMessageLinkData

object AttachmentMessageLink extends StoredFromModelFactory[AttachmentMessageLinkData, AttachmentMessageLink]
{
	// IMPLEMENTED	--------------------
	
	override def dataFactory = AttachmentMessageLinkData
	
	override protected def complete(model: HasProperties, data: AttachmentMessageLinkData) =
		model("id").tryInt.map { apply(_, data) }
}

/**
  * Represents a attachment message link that has already been stored in the database
  * @param id   id of this attachment message link in the database
  * @param data Wrapped attachment message link data
  * @author Mikko Hilpinen
  * @since 27.12.2024, v1.1
  */
case class AttachmentMessageLink(id: Int, data: AttachmentMessageLinkData) 
	extends StoredModelConvertible[AttachmentMessageLinkData] with FromIdFactory[Int, AttachmentMessageLink] 
		with AttachmentMessageLinkFactoryWrapper[AttachmentMessageLinkData, AttachmentMessageLink]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this attachment message link in the database
	  */
	def access = DbSingleAttachmentMessageLink(id)
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory = data
	
	override def withId(id: Int) = copy(id = id)
	
	override protected def wrap(data: AttachmentMessageLinkData) = copy(data = data)
}

