package vf.emissary.database.access.single.url.link_placement

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.stored.url.LinkPlacement

/**
  * An access point to individual link placements, based on their id
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
case class DbSingleLinkPlacement(id: Int) 
	extends UniqueLinkPlacementAccess with SingleIntIdModelAccess[LinkPlacement]

