package vf.emissary.database.access.many.url.link_placement

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.emissary.database.access.many.text.statement.ManyStatementPlacedAccess
import vf.emissary.database.factory.url.LinkPlacementFactory
import vf.emissary.database.model.url.LinkPlacementModel
import vf.emissary.model.stored.url.LinkPlacement

@deprecated("Moved to Logos", "v1.1")
object ManyLinkPlacementsAccess extends ViewFactory[ManyLinkPlacementsAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyLinkPlacementsAccess = 
		_ManyLinkPlacementsAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyLinkPlacementsAccess(override val accessCondition: Option[Condition]) 
		extends ManyLinkPlacementsAccess
}

/**
  * A common trait for access points which target multiple link placements at a time
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
@deprecated("Moved to Logos", "v1.1")
trait ManyLinkPlacementsAccess 
	extends ManyRowModelAccess[LinkPlacement] with ManyStatementPlacedAccess[ManyLinkPlacementsAccess] 
		with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * statement ids of the accessible link placements
	  */
	def statementIds(implicit connection: Connection) = {
		pullColumn(model.statementIdColumn).map 
		{
			 v => v.getInt 
		}
	}
	
	/**
	  * link ids of the accessible link placements
	  */
	def linkIds(implicit connection: Connection) = pullColumn(model.linkIdColumn).map { v => v.getInt }
	
	/**
	  * order indices of the accessible link placements
	  */
	def orderIndices(implicit connection: Connection) = {
		pullColumn(model.orderIndexColumn).map 
		{
			 v => v.getInt 
		}
	}
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = LinkPlacementFactory
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	override protected def model = LinkPlacementModel
	
	override protected def self = this
	
	override def apply(condition: Condition): ManyLinkPlacementsAccess = ManyLinkPlacementsAccess(condition)
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the link ids of the targeted link placements
	  * @param newLinkId A new link id to assign
	  * @return Whether any link placement was affected
	  */
	def linkIds_=(newLinkId: Int)(implicit connection: Connection) = putColumn(model.linkIdColumn, newLinkId)
	
	/**
	  * @param linkId Id of the targeted link
	  * @return Access to placements of that link
	  */
	def ofLink(linkId: Int) = filter(model.withLinkId(linkId).toCondition)
	
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

