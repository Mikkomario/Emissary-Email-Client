package vf.emissary.model.stored.messaging

import utopia.flow.generic.model.template.ModelLike.AnyModel
import utopia.logos.model.partial.text.StatementPlacementData
import utopia.logos.model.stored.text.StoredStatementPlacementLike
import utopia.vault.store.StoredFromModelFactory
import vf.emissary.database.access.single.messaging.message.link.statement.DbSingleMessageStatementLink
import vf.emissary.model.factory.messaging.MessageStatementLinkFactoryWrapper
import vf.emissary.model.partial.messaging.MessageStatementLinkData

object MessageStatementLink extends StoredFromModelFactory[MessageStatementLinkData, MessageStatementLink]
{
	// IMPLEMENTED	--------------------
	
	override def dataFactory = MessageStatementLinkData
	
	override protected def complete(model: AnyModel, data: MessageStatementLinkData) = 
		model("id").tryInt.map { apply(_, data) }
}

/**
  * Represents a message statement link that has already been stored in the database
  * @param id id of this message statement link in the database
  * @param data Wrapped message statement link data
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class MessageStatementLink(id: Int, data: MessageStatementLinkData) 
	extends MessageStatementLinkFactoryWrapper[MessageStatementLinkData, MessageStatementLink]
		with StatementPlacementData with StoredStatementPlacementLike[MessageStatementLinkData, MessageStatementLink]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this message statement link in the database
	  */
	def access = DbSingleMessageStatementLink(id)
	
	
	// IMPLEMENTED	--------------------
	
	override def withId(id: Int) = copy(id = id)
	
	override protected def wrap(data: MessageStatementLinkData) = copy(data = data)
}

