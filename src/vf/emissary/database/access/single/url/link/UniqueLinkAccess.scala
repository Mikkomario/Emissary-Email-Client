package vf.emissary.database.access.single.url.link

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.url.LinkFactory
import vf.emissary.model.stored.url.Link

object UniqueLinkAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueLinkAccess = new _UniqueLinkAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueLinkAccess(condition: Condition) extends UniqueLinkAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct links.
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
trait UniqueLinkAccess 
	extends UniqueLinkAccessLike[Link] with SingleRowModelAccess[Link] with FilterableView[UniqueLinkAccess]
{
	// IMPLEMENTED	--------------------
	
	override def factory = LinkFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueLinkAccess = 
		new UniqueLinkAccess._UniqueLinkAccess(mergeCondition(filterCondition))
}

