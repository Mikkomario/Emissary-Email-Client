package vf.emissary.database.access.single.url.request_path

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.stored.url.RequestPath

/**
  * An access point to individual request paths, based on their id
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
case class DbSingleRequestPath(id: Int) 
	extends UniqueRequestPathAccess with SingleIntIdModelAccess[RequestPath]

