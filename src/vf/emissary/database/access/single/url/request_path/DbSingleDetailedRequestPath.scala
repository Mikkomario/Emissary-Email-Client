package vf.emissary.database.access.single.url.request_path

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.combined.url.DetailedRequestPath

/**
  * An access point to individual detailed request paths, based on their request path id
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
case class DbSingleDetailedRequestPath(id: Int) 
	extends UniqueDetailedRequestPathAccess with SingleIntIdModelAccess[DetailedRequestPath]

