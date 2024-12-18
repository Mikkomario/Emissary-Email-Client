package vf.emissary.database.factory.text

import utopia.logos.database.factory.text.StatementDbFactory
import utopia.logos.model.stored.text.StoredStatement
import utopia.vault.model.immutable.DbPropertyDeclaration
import utopia.vault.nosql.factory.row.FromTimelineRowFactory
import utopia.vault.nosql.factory.row.linked.CombiningFactory
import utopia.vault.nosql.factory.row.model.FromRowModelFactory
import vf.emissary.database.factory.messaging.SubjectStatementLinkFactory
import vf.emissary.database.storable.messaging.SubjectDbModel
import vf.emissary.model.combined.text.SubjectStatement
import vf.emissary.model.stored.messaging.SubjectStatementLink

/**
  * Used for reading subject statements from the database
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
// TODO: Rename to -DbFactory
object SubjectStatementFactory 
	extends CombiningFactory[SubjectStatement, StoredStatement, SubjectStatementLink]
		with FromTimelineRowFactory[SubjectStatement]
{
	// ATTRIBUTES   --------------------
	
	private val subjectModel = SubjectDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def parentFactory: FromRowModelFactory[StoredStatement] = StatementDbFactory
	override def childFactory = SubjectStatementLinkFactory
	
	override def timestamp: DbPropertyDeclaration = subjectModel.created
	
	override def apply(parent: StoredStatement, child: SubjectStatementLink): SubjectStatement =
		SubjectStatement(parent, child)
}

