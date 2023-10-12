package vf.emissary.database.access.single.text.statement

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.combined.text.SubjectStatement

/**
  * An access point to individual subject statements, based on their statement id
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class DbSingleSubjectStatement(id: Int) 
	extends UniqueSubjectStatementAccess with SingleIntIdModelAccess[SubjectStatement]

