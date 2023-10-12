package vf.emissary.database.model.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.emissary.database.factory.messaging.MessageStatementLinkFactory
import vf.emissary.model.partial.messaging.MessageStatementLinkData
import vf.emissary.model.stored.messaging.MessageStatementLink

/**
  * Used for constructing MessageStatementLinkModel instances and for inserting message statement links
  *  to the database
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object MessageStatementLinkModel 
	extends DataInserter[MessageStatementLinkModel, MessageStatementLink, MessageStatementLinkData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains message statement link message id
	  */
	val messageIdAttName = "messageId"
	
	/**
	  * Name of the property that contains message statement link statement id
	  */
	val statementIdAttName = "statementId"
	
	/**
	  * Name of the property that contains message statement link order index
	  */
	val orderIndexAttName = "orderIndex"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains message statement link message id
	  */
	def messageIdColumn = table(messageIdAttName)
	
	/**
	  * Column that contains message statement link statement id
	  */
	def statementIdColumn = table(statementIdAttName)
	
	/**
	  * Column that contains message statement link order index
	  */
	def orderIndexColumn = table(orderIndexAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = MessageStatementLinkFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: MessageStatementLinkData) = 
		apply(None, Some(data.messageId), Some(data.statementId), Some(data.orderIndex))
	
	override protected def complete(id: Value, data: MessageStatementLinkData) = 
		MessageStatementLink(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param id A message statement link id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param messageId Id of the message where the statement was made
	  * @return A model containing only the specified message id
	  */
	def withMessageId(messageId: Int) = apply(messageId = Some(messageId))
	
	/**
	  * @param orderIndex Index of the statement in the message (0-based)
	  * @return A model containing only the specified order index
	  */
	def withOrderIndex(orderIndex: Int) = apply(orderIndex = Some(orderIndex))
	
	/**
	  * @param statementId The statement that was made
	  * @return A model containing only the specified statement id
	  */
	def withStatementId(statementId: Int) = apply(statementId = Some(statementId))
}

/**
  * Used for interacting with MessageStatementLinks in the database
  * @param id message statement link database id
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class MessageStatementLinkModel(id: Option[Int] = None, messageId: Option[Int] = None, 
	statementId: Option[Int] = None, orderIndex: Option[Int] = None) 
	extends StorableWithFactory[MessageStatementLink]
{
	// IMPLEMENTED	--------------------
	
	override def factory = MessageStatementLinkModel.factory
	
	override def valueProperties = {
		import MessageStatementLinkModel._
		Vector("id" -> id, messageIdAttName -> messageId, statementIdAttName -> statementId, 
			orderIndexAttName -> orderIndex)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param messageId Id of the message where the statement was made
	  * @return A new copy of this model with the specified message id
	  */
	def withMessageId(messageId: Int) = copy(messageId = Some(messageId))
	
	/**
	  * @param orderIndex Index of the statement in the message (0-based)
	  * @return A new copy of this model with the specified order index
	  */
	def withOrderIndex(orderIndex: Int) = copy(orderIndex = Some(orderIndex))
	
	/**
	  * @param statementId The statement that was made
	  * @return A new copy of this model with the specified statement id
	  */
	def withStatementId(statementId: Int) = copy(statementId = Some(statementId))
}

