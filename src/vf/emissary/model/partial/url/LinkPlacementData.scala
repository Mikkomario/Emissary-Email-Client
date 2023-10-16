package vf.emissary.model.partial.url

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.template.ModelConvertible

object LinkPlacementData extends FromModelFactoryWithSchema[LinkPlacementData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("statementId", IntType, Vector("statement_id")), 
			PropertyDeclaration("linkId", IntType, Vector("link_id")), PropertyDeclaration("orderIndex", 
			IntType, Vector("order_index"))))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		LinkPlacementData(valid("statementId").getInt, valid("linkId").getInt, valid("orderIndex").getInt)
}

/**
  * Places a link within a statement
  * @param statementId Id of the statement where the specified link is referenced
  * @param linkId Referenced link
  * @param orderIndex Index where the link appears in the statement (0-based)
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
case class LinkPlacementData(statementId: Int, linkId: Int, orderIndex: Int) extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("statementId" -> statementId, "linkId" -> linkId, "orderIndex" -> orderIndex))
}

