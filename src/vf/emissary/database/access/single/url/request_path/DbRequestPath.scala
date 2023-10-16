package vf.emissary.database.access.single.url.request_path

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.url.RequestPathFactory
import vf.emissary.database.model.url.RequestPathModel
import vf.emissary.model.stored.url.RequestPath

/**
  * Used for accessing individual request paths
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
object DbRequestPath extends SingleRowModelAccess[RequestPath] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = RequestPathModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = RequestPathFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted request path
	  * @return An access point to that request path
	  */
	def apply(id: Int) = DbSingleRequestPath(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique request paths.
	  * @return An access point to the request path that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = UniqueRequestPathAccess(mergeCondition(condition))
}

