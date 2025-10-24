package vf.emissary.model.stored.messaging

import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import utopia.vault.store.{FromIdFactory, StoredFromModelFactory, StoredModelConvertible}
import vf.emissary.database.access.single.messaging.thread.link.subject.DbSingleMessageThreadSubjectLink
import vf.emissary.model.factory.messaging.MessageThreadSubjectLinkFactoryWrapper
import vf.emissary.model.partial.messaging.MessageThreadSubjectLinkData

object MessageThreadSubjectLink 
	extends StoredFromModelFactory[MessageThreadSubjectLinkData, MessageThreadSubjectLink]
{
	// IMPLEMENTED	--------------------
	
	override def dataFactory = MessageThreadSubjectLinkData
	
	override protected def complete(model: HasProperties, data: MessageThreadSubjectLinkData) =
		model("id").tryInt.map { apply(_, data) }
}

/**
  * Represents a message thread subject link that has already been stored in the database
  * @param id id of this message thread subject link in the database
  * @param data Wrapped message thread subject link data
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class MessageThreadSubjectLink(id: Int, data: MessageThreadSubjectLinkData) 
	extends StoredModelConvertible[MessageThreadSubjectLinkData] 
		with FromIdFactory[Int, MessageThreadSubjectLink] 
		with MessageThreadSubjectLinkFactoryWrapper[MessageThreadSubjectLinkData, MessageThreadSubjectLink]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this message thread subject link in the database
	  */
	def access = DbSingleMessageThreadSubjectLink(id)
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory = data
	
	override def withId(id: Int) = copy(id = id)
	
	override protected def wrap(data: MessageThreadSubjectLinkData) = copy(data = data)
}

