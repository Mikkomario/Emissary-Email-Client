package vf.emissary.database.factory.text

import utopia.flow.generic.model.template.ModelLike.AnyModel
import utopia.vault.model.immutable.Table
import utopia.vault.sql.OrderBy
import vf.emissary.database.props.text.StatementPlacementDbProps
import vf.emissary.model.partial.text.StatementPlacementData
import vf.emissary.model.stored.text.StatementPlacement

object StatementPlacementDbFactory
{
	// OTHER	--------------------
	
	/**
	  * @param table Table from which data is read
	  * @param dbProps Database properties used when reading column data
	  * @return A factory used for parsing statement placements from database model data
	  */
	def apply(table: Table, dbProps: StatementPlacementDbProps): StatementPlacementDbFactory = 
		_StatementPlacementDbFactory(table, dbProps)
	
	
	// NESTED	--------------------
	
	/**
	  * @param table Table from which data is read
	  * @param dbProps Database properties used when reading column data
	  */
	private case class _StatementPlacementDbFactory(table: Table, dbProps: StatementPlacementDbProps) 
		extends StatementPlacementDbFactory
	{
		// IMPLEMENTED	--------------------
		
		override def defaultOrdering: Option[OrderBy] = None
		
		/**
		  * @param model Model from which additional data may be read
		  * @param id Id to assign to the read/parsed statement placement
		  * @param parentId parent id to assign to the new statement placement
		  * @param statementId statement id to assign to the new statement placement
		  * @param orderIndex order index to assign to the new statement placement
		  */
		override protected def apply(model: AnyModel, id: Int, parentId: Int, statementId: Int, 
			orderIndex: Int) = 
			StatementPlacement(id, StatementPlacementData(parentId, statementId, orderIndex))
	}
}

/**
  * Common trait for factories which parse statement placement data from database-originated models
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait StatementPlacementDbFactory extends StatementPlacementDbFactoryLike[StatementPlacement]

