package vf.emissary.database.access.many.url.link

import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.sql.Condition
import vf.emissary.database.factory.url.LinkFactory
import vf.emissary.model.stored.url.Link

object ManyLinksAccess
{
	// NESTED	--------------------
	
	private class ManyLinksSubView(condition: Condition) extends ManyLinksAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple links at a time
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
trait ManyLinksAccess extends ManyLinksAccessLike[Link, ManyLinksAccess] with ManyRowModelAccess[Link]
{
	// IMPLEMENTED	--------------------
	
	override def factory = LinkFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyLinksAccess = 
		new ManyLinksAccess.ManyLinksSubView(mergeCondition(filterCondition))
}

