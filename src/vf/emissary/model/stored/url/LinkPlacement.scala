package vf.emissary.model.stored.url

import utopia.vault.model.template.StoredModelConvertible
import vf.emissary.database.access.single.url.link_placement.DbSingleLinkPlacement
import vf.emissary.model.partial.url.LinkPlacementData

/**
  * Represents a link placement that has already been stored in the database
  * @param id id of this link placement in the database
  * @param data Wrapped link placement data
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
case class LinkPlacement(id: Int, data: LinkPlacementData) extends StoredModelConvertible[LinkPlacementData]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this link placement in the database
	  */
	def access = DbSingleLinkPlacement(id)
}

