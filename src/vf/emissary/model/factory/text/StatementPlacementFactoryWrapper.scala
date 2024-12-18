package vf.emissary.model.factory.text

import utopia.logos.model.factory.text.TextPlacementFactoryWrapper

/**
  * Common
  * 
	 trait for classes that implement StatementPlacementFactory by wrapping a StatementPlacementFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait StatementPlacementFactoryWrapper[A <: StatementPlacementFactory[A], +Repr] 
	extends StatementPlacementFactory[Repr] with TextPlacementFactoryWrapper[A, Repr]
{
	// IMPLEMENTED	--------------------
	
	override def withStatementId(statementId: Int) = withPlacedId(statementId)
}

