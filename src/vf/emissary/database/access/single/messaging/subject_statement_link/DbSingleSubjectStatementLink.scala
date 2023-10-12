package vf.emissary.database.access.single.messaging.subject_statement_link

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.stored.messaging.SubjectStatementLink

/**
  * An access point to individual subject statement links, based on their id
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class DbSingleSubjectStatementLink(id: Int) 
	extends UniqueSubjectStatementLinkAccess with SingleIntIdModelAccess[SubjectStatementLink]

