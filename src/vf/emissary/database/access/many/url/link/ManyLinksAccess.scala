package vf.emissary.database.access.many.url.link

import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.emissary.database.access.many.url.request_path.DbRequestPaths
import vf.emissary.database.factory.url.LinkFactory
import vf.emissary.model.combined.url.DetailedLink
import vf.emissary.model.stored.url.Link

object ManyLinksAccess extends ViewFactory[ManyLinksAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyLinksAccess = _ManyLinksAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	case class _ManyLinksAccess(override val accessCondition: Option[Condition]) extends ManyLinksAccess
}

/**
  * A common trait for access points which target multiple links at a time
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
trait ManyLinksAccess extends ManyLinksAccessLike[Link, ManyLinksAccess] with ManyRowModelAccess[Link]
{
	// COMPUTED	--------------------
	
	/**
	  * Pulls the accessible links as a map
	  * @param connection Implicit DB donnection
	  */
	def toMap(implicit connection: Connection) = {
			pullColumnMultiMap(model.requestPathIdColumn, model.queryParametersColumn).map 
			{
				 case (pathIdVal, 
						paramVals) => pathIdVal.getInt -> paramVals.map { _.getModel } 
			}
	}
	
	/**
	  * All accessible links, including request path and domain information
	  * @param connection Implicit DB connection
	  */
	def pullDetailed(implicit connection: Connection) = {
		val links = pull
		if (links.nonEmpty) {
			// Pulls associated request paths
			val pathMap = DbRequestPaths(links.map { _.requestPathId }.toSet).pullDetailed
				.view.map { p => p.id -> p }.toMap
			// Combines the links with the paths
			links.map { link => DetailedLink(link, pathMap(link.requestPathId)) }
		}
		else
			Vector()
	}
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = LinkFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): ManyLinksAccess = ManyLinksAccess(condition)
}

