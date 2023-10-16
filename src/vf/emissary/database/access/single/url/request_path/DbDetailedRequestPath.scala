package vf.emissary.database.access.single.url.request_path

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.url.DetailedRequestPathFactory
import vf.emissary.database.model.url.{DomainModel, RequestPathModel}
import vf.emissary.model.combined.url.DetailedRequestPath

/**
  * Used for accessing individual detailed request paths
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
object DbDetailedRequestPath 
	extends SingleRowModelAccess[DetailedRequestPath] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * A database model (factory) used for interacting with linked request paths
	  */
	protected def model = RequestPathModel
	
	/**
	  * A database model (factory) used for interacting with the linked domain
	  */
	protected def domainModel = DomainModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = DetailedRequestPathFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted detailed request path
	  * @return An access point to that detailed request path
	  */
	def apply(id: Int) = DbSingleDetailedRequestPath(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique detailed request paths.
	  * @return An access point to the detailed request path that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = 
		UniqueDetailedRequestPathAccess(mergeCondition(condition))
}

