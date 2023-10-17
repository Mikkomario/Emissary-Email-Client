package vf.emissary.model.partial.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.template.ModelConvertible
import vf.emissary.model.template.Placed

object SubjectStatementLinkData extends FromModelFactoryWithSchema[SubjectStatementLinkData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("subjectId", IntType, Vector("subject_id")), 
			PropertyDeclaration("statementId", IntType, Vector("statement_id")), 
			PropertyDeclaration("orderIndex", IntType, Vector("order_index"))))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		SubjectStatementLinkData(valid("subjectId").getInt, valid("statementId").getInt, 
			valid("orderIndex").getInt)
}

/**
  * Connects a message thread subject to the statements made within that subject
  * @param subjectId Id of the described subject
  * @param statementId Id of the statement made within the referenced subject
  * @param orderIndex Index where this statement appears within the referenced subject (0-based)
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class SubjectStatementLinkData(subjectId: Int, statementId: Int, orderIndex: Int) 
	extends ModelConvertible with Placed
{
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("subjectId" -> subjectId, "statementId" -> statementId, "orderIndex" -> orderIndex))
}

