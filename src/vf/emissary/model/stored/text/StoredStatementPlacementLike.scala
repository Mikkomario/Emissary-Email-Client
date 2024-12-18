package vf.emissary.model.stored.text

import utopia.logos.model.partial.text.TextPlacementData
import utopia.logos.model.stored.text.StoredTextPlacementLike
import vf.emissary.model.factory.text.StatementPlacementFactoryWrapper
import vf.emissary.model.partial.text.StatementPlacementDataLike

/**
  * Common trait for statement placements which have been stored in the database
  * @tparam Data Type of the wrapped data
  * @tparam Repr Implementing type
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait StoredStatementPlacementLike[Data <: StatementPlacementDataLike[Data], +Repr <: TextPlacementData] 
	extends StatementPlacementFactoryWrapper[Data, Repr] with StatementPlacementDataLike[Repr] 
		with StoredTextPlacementLike[Data, Repr]
{
	// IMPLEMENTED	--------------------
	
	override def statementId = data.statementId
	
	override protected def wrappedFactory = data
	
	override def copyStatementPlacement(parentId: Int, statementId: Int, orderIndex: Int) = 
		wrap(data.copyStatementPlacement(parentId, statementId, orderIndex))
}

