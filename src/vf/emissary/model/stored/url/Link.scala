package vf.emissary.model.stored.url

import utopia.vault.model.template.StoredModelConvertible
import vf.emissary.database.access.single.url.link.DbSingleLink
import vf.emissary.model.partial.url.LinkData

/**
  * Represents a link that has already been stored in the database
  * @param id id of this link in the database
  * @param data Wrapped link data
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
case class Link(id: Int, data: LinkData) extends StoredModelConvertible[LinkData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this link in the database
	  */
	def access = DbSingleLink(id)
}

