package vf.emissary.database.factory.text

import utopia.vault.nosql.factory.row.FromRowFactoryWithTimestamps
import utopia.vault.nosql.factory.row.linked.CombiningFactory
import vf.emissary.database.factory.messaging.MessageStatementLinkFactory
import vf.emissary.model.combined.text.MessageStatement
import vf.emissary.model.stored.messaging.MessageStatementLink
import vf.emissary.model.stored.text.Statement

/**
  * Used for reading message statements from the database
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object MessageStatementFactory 
	extends CombiningFactory[MessageStatement, Statement, MessageStatementLink] 
		with FromRowFactoryWithTimestamps[MessageStatement]
{
	// IMPLEMENTED	--------------------
	
	override def childFactory = MessageStatementLinkFactory
	
	override def creationTimePropertyName = StatementFactory.creationTimePropertyName
	
	override def parentFactory = StatementFactory
	
	override def apply(statement: Statement, messageLink: MessageStatementLink) = 
		MessageStatement(statement, messageLink)
}

