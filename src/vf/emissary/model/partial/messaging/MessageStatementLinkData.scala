package vf.emissary.model.partial.messaging

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.template.ModelConvertible
import vf.emissary.model.factory.messaging.MessageStatementLinkFactory
import vf.emissary.model.partial.text.{StatementPlacementData, StatementPlacementDataLike}

object MessageStatementLinkData extends FromModelFactoryWithSchema[MessageStatementLinkData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("messageId", IntType, Vector("message_id", "parentId", "parent_id")),
		PropertyDeclaration("statementId", IntType, Single("statement_id")),
		PropertyDeclaration("orderIndex", IntType, Single("order_index"), 0)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		MessageStatementLinkData(valid("messageId").getInt, valid("statementId").getInt, 
			valid("orderIndex").getInt)
}

/**
  * Documents a statement made within a message
  * @param messageId Id of the message where the statement was made
  * @param statementId Id of the placed statement
  * @param orderIndex 0-based index that indicates the specific location of the placed text
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class MessageStatementLinkData(messageId: Int, statementId: Int, orderIndex: Int = 0) 
	extends MessageStatementLinkFactory[MessageStatementLinkData] with StatementPlacementData 
		with StatementPlacementDataLike[MessageStatementLinkData] with ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def parentId = messageId
	
	override def toModel = 
		Model(Vector("messageId" -> messageId, "statementId" -> statementId, "orderIndex" -> orderIndex))
	
	override def copyStatementPlacement(parentId: Int, statementId: Int, orderIndex: Int) = 
		copy(messageId = parentId, statementId = statementId, orderIndex = orderIndex)
	
	override def withMessageId(messageId: Int) = copy(messageId = messageId)
}

