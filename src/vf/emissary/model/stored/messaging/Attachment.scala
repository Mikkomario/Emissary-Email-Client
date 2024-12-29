package vf.emissary.model.stored.messaging

import utopia.flow.generic.model.template.ModelLike.AnyModel
import utopia.vault.model.template.{FromIdFactory, StoredFromModelFactory, StoredModelConvertible}
import vf.emissary.database.access.single.messaging.attachment.DbSingleAttachment
import vf.emissary.model.factory.messaging.AttachmentFactoryWrapper
import vf.emissary.model.partial.messaging.AttachmentData

object Attachment extends StoredFromModelFactory[AttachmentData, Attachment]
{
	// IMPLEMENTED	--------------------
	
	override def dataFactory = AttachmentData
	
	override protected def complete(model: AnyModel, data: AttachmentData) = 
		model("id").tryInt.map { apply(_, data) }
}

/**
  * Represents a attachment that has already been stored in the database
  * @param id   id of this attachment in the database
  * @param data Wrapped attachment data
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
case class Attachment(id: Int, data: AttachmentData) 
	extends StoredModelConvertible[AttachmentData] with FromIdFactory[Int, Attachment] 
		with AttachmentFactoryWrapper[AttachmentData, Attachment]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this attachment in the database
	  */
	def access = DbSingleAttachment(id)
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory = data
	
	override def withId(id: Int) = copy(id = id)
	
	override protected def wrap(data: AttachmentData) = copy(data = data)
}

