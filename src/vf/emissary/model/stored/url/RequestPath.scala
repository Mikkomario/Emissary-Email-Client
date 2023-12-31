package vf.emissary.model.stored.url

import utopia.vault.model.template.StoredModelConvertible
import vf.emissary.database.access.single.url.request_path.DbSingleRequestPath
import vf.emissary.model.partial.url.RequestPathData

/**
  * Represents a request path that has already been stored in the database
  * @param id id of this request path in the database
  * @param data Wrapped request path data
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
case class RequestPath(id: Int, data: RequestPathData) extends StoredModelConvertible[RequestPathData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this request path in the database
	  */
	def access = DbSingleRequestPath(id)
}

