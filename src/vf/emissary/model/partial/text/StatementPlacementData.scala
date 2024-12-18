package vf.emissary.model.partial.text

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.logos.model.partial.text.{TextPlacementData, TextPlacementDataLike}

object StatementPlacementData extends FromModelFactoryWithSchema[StatementPlacementData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("parentId", IntType, Single("parent_id")), 
			PropertyDeclaration("statementId", IntType, Vector("placedId", "placed_id", "statement_id")), 
			PropertyDeclaration("orderIndex", IntType, Single("order_index"), 0)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		StatementPlacementData(valid("parentId").getInt, valid("statementId").getInt, 
			valid("orderIndex").getInt)
	
	
	// OTHER	--------------------
	
	/**
	  * Creates a new statement placement
	  * @param parentId Id of the text where the placed text appears
	  * @param statementId Id of the placed statement
	  * @param orderIndex 0-based index that indicates the specific location of the placed text
	  * @return statement placement with the specified properties
	  */
	def apply(parentId: Int, statementId: Int, orderIndex: Int = 0): StatementPlacementData = 
		_StatementPlacementData(parentId, statementId, orderIndex)
	
	
	// NESTED	--------------------
	
	/**
	  * Concrete implementation of the statement placement data trait
	  * @param parentId Id of the text where the placed text appears
	  * @param statementId Id of the placed statement
	  * @param orderIndex 0-based index that indicates the specific location of the placed text
	  * @author Mikko Hilpinen
	  * @since 17.12.2024
	  */
	private case class _StatementPlacementData(parentId: Int, statementId: Int, orderIndex: Int = 0) 
		extends StatementPlacementData
	{
		// IMPLEMENTED	--------------------
		
		/**
		  * @param parentId Id of the text where the placed text appears
		  * @param statementId Id of the placed statement
		  * @param orderIndex 0-based index that indicates the specific location of the placed text
		  */
		override def copyStatementPlacement(parentId: Int, statementId: Int, orderIndex: Int = 0) = 
			_StatementPlacementData(parentId, statementId, orderIndex)
	}
}

/**
  * Places a statement within some text
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait StatementPlacementData 
	extends StatementPlacementDataLike[StatementPlacementData] with TextPlacementData 
		with TextPlacementDataLike[StatementPlacementData]
{
	// IMPLEMENTED	--------------------
	
	override def placedId = statementId
}

