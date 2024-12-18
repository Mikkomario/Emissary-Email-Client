package vf.emissary.database.access.single.text.placement.statement

import utopia.logos.database.access.single.text.placement.UniqueTextPlacementAccessLike
import utopia.vault.database.Connection
import vf.emissary.database.props.text.StatementPlacementDbProps

/**
  * A common trait for access points which target individual statement placements or similar items at a time
  * @tparam A Type of read (statement placements -like) instances
  * @tparam Repr Type of this access point
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait UniqueStatementPlacementAccessLike[+A, +Repr] extends UniqueTextPlacementAccessLike[A, Repr]
{
	// ABSTRACT	--------------------
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	override protected def model: StatementPlacementDbProps
	
	
	// COMPUTED	--------------------
	
	/**
	  * Id of the placed statement. 
	  * None if no statement placement (or value) was found.
	  */
	def statementId(implicit connection: Connection) = placedId
}

