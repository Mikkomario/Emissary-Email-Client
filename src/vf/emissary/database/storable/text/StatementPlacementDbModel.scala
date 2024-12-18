package vf.emissary.database.storable.text

import utopia.logos.database.storable.text.TextPlacementDbModel
import utopia.vault.model.immutable.Table
import vf.emissary.database.props.text.StatementPlacementDbProps

object StatementPlacementDbModel
{
	// OTHER	--------------------
	
	/**
	  * @param table The primarily targeted table
	  * @param props Targeted database properties
	  * @return A factory used for constructing statement placement models using the specified configuration
	  */
	def factory(table: Table, props: StatementPlacementDbProps) = StatementPlacementDbModelFactory(table, 
		props)
}

/**
  * Common trait for database interaction models dealing with statement placements
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait StatementPlacementDbModel 
	extends StatementPlacementDbModelLike[StatementPlacementDbModel] with TextPlacementDbModel

