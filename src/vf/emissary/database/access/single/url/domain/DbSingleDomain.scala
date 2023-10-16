package vf.emissary.database.access.single.url.domain

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.stored.url.Domain

/**
  * An access point to individual domains, based on their id
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
case class DbSingleDomain(id: Int) extends UniqueDomainAccess with SingleIntIdModelAccess[Domain]

