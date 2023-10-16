package vf.emissary.database.factory.url

import utopia.vault.nosql.factory.row.linked.CombiningFactory
import vf.emissary.model.combined.url.DetailedRequestPath
import vf.emissary.model.stored.url.{Domain, RequestPath}

/**
  * Used for reading detailed request paths from the database
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
object DetailedRequestPathFactory extends CombiningFactory[DetailedRequestPath, RequestPath, Domain]
{
	// IMPLEMENTED	--------------------
	
	override def childFactory = DomainFactory
	
	override def parentFactory = RequestPathFactory
	
	override def apply(requestPath: RequestPath, domain: Domain) = DetailedRequestPath(requestPath, domain)
}

