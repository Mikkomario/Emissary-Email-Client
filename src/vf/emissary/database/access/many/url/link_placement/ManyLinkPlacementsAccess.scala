package vf.emissary.database.access.many.url.link_placement

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.url.LinkPlacementFactory
import vf.emissary.database.model.url.LinkPlacementModel
import vf.emissary.model.stored.url.LinkPlacement

object ManyLinkPlacementsAccess
{
	// NESTED	--------------------
	
	private class ManyLinkPlacementsSubView(condition: Condition) extends ManyLinkPlacementsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple link placements at a time
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
trait ManyLinkPlacementsAccess 
	extends ManyRowModelAccess[LinkPlacement] with FilterableView[ManyLinkPlacementsAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * statement ids of the accessible link placements
	  */
	def statementIds(implicit connection: Connection) = pullColumn(model.statementIdColumn)
		.map { v => v.getInt }
	
	/**
	  * link ids of the accessible link placements
	  */
	def linkIds(implicit connection: Connection) = pullColumn(model.linkIdColumn).map { v => v.getInt }
	
	/**
	  * order indices of the accessible link placements
	  */
	def orderIndices(implicit connection: Connection) = pullColumn(model.orderIndexColumn)
		.map { v => v.getInt }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = LinkPlacementModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = LinkPlacementFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyLinkPlacementsAccess = 
		new ManyLinkPlacementsAccess.ManyLinkPlacementsSubView(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the link ids of the targeted link placements
	  * @param newLinkId A new link id to assign
	  * @return Whether any link placement was affected
	  */
	def linkIds_=(newLinkId: Int)(implicit connection: Connection) = putColumn(model.linkIdColumn, newLinkId)
	
	/**
	  * Updates the order indices of the targeted link placements
	  * @param newOrderIndex A new order index to assign
	  * @return Whether any link placement was affected
	  */
	def orderIndices_=(newOrderIndex: Int)(implicit connection: Connection) = 
		putColumn(model.orderIndexColumn, newOrderIndex)
	
	/**
	  * Updates the statement ids of the targeted link placements
	  * @param newStatementId A new statement id to assign
	  * @return Whether any link placement was affected
	  */
	def statementIds_=(newStatementId: Int)(implicit connection: Connection) = 
		putColumn(model.statementIdColumn, newStatementId)
}

