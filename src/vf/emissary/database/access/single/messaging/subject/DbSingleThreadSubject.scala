package vf.emissary.database.access.single.messaging.subject

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.combined.messaging.ThreadSubject

/**
  * An access point to individual thread subjects, based on their subject id
  * @author Mikko Hilpinen
  * @since 17.10.2023, v0.1
  */
case class DbSingleThreadSubject(id: Int) 
	extends UniqueThreadSubjectAccess with SingleIntIdModelAccess[ThreadSubject]

