package vf.emissary.model.partial.messaging

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.template.ModelConvertible
import vf.emissary.model.factory.messaging.SubjectStatementLinkFactory
import vf.emissary.model.partial.text.{StatementPlacementData, StatementPlacementDataLike}

object SubjectStatementLinkData extends FromModelFactoryWithSchema[SubjectStatementLinkData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("subjectId", IntType, Vector("parentId", "parent_id", "subject_id")),
		PropertyDeclaration("statementId", IntType, Single("statement_id")),
		PropertyDeclaration("orderIndex", IntType, Single("order_index"), 0)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		SubjectStatementLinkData(valid("subjectId").getInt, valid("statementId").getInt, 
			valid("orderIndex").getInt)
}

/**
  * Connects a message thread subject to the statements made within that subject
  * @param subjectId Id of the described subject
  * @param statementId Id of the placed statement
  * @param orderIndex 0-based index that indicates the specific location of the placed text
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class SubjectStatementLinkData(subjectId: Int, statementId: Int, orderIndex: Int = 0) 
	extends SubjectStatementLinkFactory[SubjectStatementLinkData] with StatementPlacementData 
		with StatementPlacementDataLike[SubjectStatementLinkData] with ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def parentId = subjectId
	
	override def toModel =
		Model(Vector("subjectId" -> subjectId, "statementId" -> statementId, "orderIndex" -> orderIndex))
	
	override def copyStatementPlacement(parentId: Int, statementId: Int, orderIndex: Int) = 
		copy(subjectId = parentId, statementId = statementId, orderIndex = orderIndex)
	
	override def withSubjectId(subjectId: Int) = copy(subjectId = subjectId)
}

