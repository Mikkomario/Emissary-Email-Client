package vf.emissary.model.partial.text

import utopia.logos.model.partial.text.HasTextPlacementProps

/**
  * Common trait for classes which provide access to statement placement properties
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait HasStatementPlacementProps extends HasTextPlacementProps
{
	// ABSTRACT	--------------------
	
	/**
	  * Id of the placed statement
	  */
	def statementId: Int
	
	
	// IMPLEMENTED	--------------------
	
	override def placedId = statementId
}

