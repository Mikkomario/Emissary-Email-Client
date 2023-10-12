package vf.emissary.database.access.single.messaging.subject

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.stored.messaging.Subject

/**
  * An access point to individual subjects, based on their id
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class DbSingleSubject(id: Int) extends UniqueSubjectAccess with SingleIntIdModelAccess[Subject]

