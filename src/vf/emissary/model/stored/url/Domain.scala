package vf.emissary.model.stored.url

import utopia.vault.model.template.StoredModelConvertible
import vf.emissary.database.access.single.url.domain.DbSingleDomain
import vf.emissary.model.partial.url.DomainData

/**
  * Represents a domain that has already been stored in the database
  * @param id id of this domain in the database
  * @param data Wrapped domain data
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
case class Domain(id: Int, data: DomainData) extends StoredModelConvertible[DomainData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this domain in the database
	  */
	def access = DbSingleDomain(id)
}

