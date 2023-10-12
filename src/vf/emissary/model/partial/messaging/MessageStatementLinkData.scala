package vf.emissary.model.partial.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.template.ModelConvertible

object MessageStatementLinkData extends FromModelFactoryWithSchema[MessageStatementLinkData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("messageId", IntType, Vector("message_id")), 
			PropertyDeclaration("statementId", IntType, Vector("statement_id")), 
			PropertyDeclaration("orderIndex", IntType, Vector("order_index"))))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		MessageStatementLinkData(valid("messageId").getInt, valid("statementId").getInt, 
			valid("orderIndex").getInt)
}

/**
  * Documents a statement made within a message
  * @param messageId Id of the message where the statement was made
  * @param statementId The statement that was made
  * @param orderIndex Index of the statement in the message (0-based)
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class MessageStatementLinkData(messageId: Int, statementId: Int, orderIndex: Int) 
	extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("messageId" -> messageId, "statementId" -> statementId, "orderIndex" -> orderIndex))
}

