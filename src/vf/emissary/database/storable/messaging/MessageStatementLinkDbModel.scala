package vf.emissary.database.storable.messaging

import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.DbPropertyDeclaration
import vf.emissary.database.EmissaryTables
import vf.emissary.database.props.text.StatementPlacementDbProps
import vf.emissary.database.storable.text.{StatementPlacementDbModel, StatementPlacementDbModelFactoryLike, StatementPlacementDbModelLike}
import vf.emissary.model.factory.messaging.MessageStatementLinkFactory
import vf.emissary.model.partial.messaging.MessageStatementLinkData
import vf.emissary.model.stored.messaging.MessageStatementLink

/**
  * Used
  * 
	 for constructing MessageStatementLinkDbModel instances and for inserting message statement links to the database
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object MessageStatementLinkDbModel 
	extends StatementPlacementDbModelFactoryLike[MessageStatementLinkDbModel, MessageStatementLink, MessageStatementLinkData] 
		with MessageStatementLinkFactory[MessageStatementLinkDbModel] with StatementPlacementDbProps
{
	// ATTRIBUTES	--------------------
	
	override lazy val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with message ids
	  */
	lazy val messageId = property("messageId")
	
	/**
	  * Database property used for interacting with statement ids
	  */
	override lazy val statementId = property("statementId")
	
	/**
	  * Database property used for interacting with order indices
	  */
	override lazy val orderIndex = property("orderIndex")
	
	
	// IMPLEMENTED	--------------------
	
	override def parentId = messageId
	
	override def table = EmissaryTables.messageStatementLink
	
	override def apply(data: MessageStatementLinkData): MessageStatementLinkDbModel = 
		apply(None, Some(data.messageId), Some(data.statementId), Some(data.orderIndex))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	/**
	  * @param messageId Id of the message where the statement was made
	  * @return A model containing only the specified message id
	  */
	override def withMessageId(messageId: Int) = apply(messageId = Some(messageId))
	
	/**
	  * @param orderIndex 0-based index that indicates the specific location of the placed text
	  * @return A model containing only the specified order index
	  */
	override def withOrderIndex(orderIndex: Int) = apply(orderIndex = Some(orderIndex))
	
	/**
	  * @param statementId Id of the placed statement
	  * @return A model containing only the specified statement id
	  */
	override def withStatementId(statementId: Int) = apply(statementId = Some(statementId))
	
	override protected def complete(id: Value, data: MessageStatementLinkData) = 
		MessageStatementLink(id.getInt, data)
}

/**
  * Used for interacting with MessageStatementLinks in the database
  * @param id message statement link database id
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
case class MessageStatementLinkDbModel(id: Option[Int] = None, messageId: Option[Int] = None, 
	statementId: Option[Int] = None, orderIndex: Option[Int] = None) 
	extends StatementPlacementDbModel with StatementPlacementDbModelLike[MessageStatementLinkDbModel] 
		with MessageStatementLinkFactory[MessageStatementLinkDbModel]
{
	// IMPLEMENTED	--------------------
	
	override def dbProps = MessageStatementLinkDbModel
	
	override def parentId = messageId
	
	override def table = MessageStatementLinkDbModel.table
	
	/**
	  * @param id Id to assign to the new model (default = currently assigned id)
	  * @param parentId parent id to assign to the new model (default = currently assigned value)
	  * @param statementId statement id to assign to the new model (default = currently assigned value)
	  * @param orderIndex order index to assign to the new model (default = currently assigned value)
	  */
	override def copyStatementPlacement(id: Option[Int] = id, parentId: Option[Int] = parentId, 
		statementId: Option[Int] = statementId, orderIndex: Option[Int] = orderIndex) = 
		copy(id = id, messageId = parentId, statementId = statementId, orderIndex = orderIndex)
	
	/**
	  * @param messageId Id of the message where the statement was made
	  * @return A new copy of this model with the specified message id
	  */
	override def withMessageId(messageId: Int) = copy(messageId = Some(messageId))
}

