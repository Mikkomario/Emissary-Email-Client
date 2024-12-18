package vf.emissary.database.access.single.messaging.subject.link.statement

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.stored.messaging.SubjectStatementLink

/**
  * An access point to individual subject statement links, based on their id
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
case class DbSingleSubjectStatementLink(id: Int) 
	extends UniqueSubjectStatementLinkAccess with SingleIntIdModelAccess[SubjectStatementLink]

