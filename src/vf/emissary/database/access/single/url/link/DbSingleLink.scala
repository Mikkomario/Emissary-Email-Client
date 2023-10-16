package vf.emissary.database.access.single.url.link

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.stored.url.Link

/**
  * An access point to individual links, based on their id
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
case class DbSingleLink(id: Int) extends UniqueLinkAccess with SingleIntIdModelAccess[Link]

