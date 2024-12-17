package vf.emissary.database.access.many.url.request_path

import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.emissary.database.access.many.url.domain.DbDomains
import vf.emissary.database.factory.url.RequestPathFactory
import vf.emissary.model.combined.url.DetailedRequestPath
import vf.emissary.model.stored.url.RequestPath

object ManyRequestPathsAccess extends ViewFactory[ManyRequestPathsAccess]
{
	// INITIAL CODE	--------------------
	
override
	
	
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): ManyRequestPathsAccess = _ManyRequestPathsAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyRequestPathsAccess(override val accessCondition: Option[Condition]) 
		extends ManyRequestPathsAccess
}

/**
  * A common trait for access points which target multiple request paths at a time
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
trait ManyRequestPathsAccess 
	extends ManyRequestPathsAccessLike[RequestPath, ManyRequestPathsAccess] 
		with ManyRowModelAccess[RequestPath]
{
	// COMPUTED	--------------------
	
	/**
	  * Copy of this access point that includes domain information
	  */
	def detailed = DbDetailedRequestPaths.filter(accessCondition)
	
	/**
	  * All accessible request paths, including domain information
	  * @param connection Implicit DB connection
	  */
	def pullDetailed(implicit connection: Connection) = {
		val paths = pull
		if (paths.nonEmpty) {
			// Pulls the associated domains
			val domainMap = DbDomains(paths.map { _.domainId }.toSet).toMapBy { _.id }
			// Combines the information together
			paths.map { p => DetailedRequestPath(p, domainMap(p.domainId)) }
		}
		else
			Vector()
	}
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = RequestPathFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): ManyRequestPathsAccess = ManyRequestPathsAccess(condition)
}

