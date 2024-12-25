package vf.emissary.database.factory.text

import utopia.logos.database.factory.text.StatementDbFactory
import utopia.logos.model.stored.text.StoredStatement
import utopia.vault.nosql.factory.row.linked.CombiningFactory
import utopia.vault.nosql.factory.row.model.FromRowModelFactory
import vf.emissary.database.factory.messaging.SubjectStatementLinkFactory
import vf.emissary.model.combined.text.SubjectStatement
import vf.emissary.model.stored.messaging.SubjectStatementLink

/**
  * Used for reading subject statements from the database
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object SubjectStatementDbFactory
	extends CombiningFactory[SubjectStatement, StoredStatement, SubjectStatementLink]
{
	// IMPLEMENTED	--------------------
	
	override def parentFactory: FromRowModelFactory[StoredStatement] = StatementDbFactory
	override def childFactory = SubjectStatementLinkFactory
	
	override def apply(parent: StoredStatement, child: SubjectStatementLink): SubjectStatement =
		SubjectStatement(parent, child)
}

