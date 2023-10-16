package vf.emissary.model.combined.url

import utopia.flow.view.template.Extender
import vf.emissary.model.partial.url.RequestPathData
import vf.emissary.model.stored.url.{Domain, RequestPath}

/**
  * Includes textual domain information in a request path
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
case class DetailedRequestPath(requestPath: RequestPath, domain: Domain) extends Extender[RequestPathData]
{
	// COMPUTED	--------------------
	
	/**
	  * Id of this request path in the database
	  */
	def id = requestPath.id
	
	
	// IMPLEMENTED	--------------------
	
	override def wrapped = requestPath.data
}

