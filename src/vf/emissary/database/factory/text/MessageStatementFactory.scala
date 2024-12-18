package vf.emissary.database.factory.text

import utopia.logos.database.factory.text.PlacedStatementDbFactoryLike
import utopia.logos.model.stored.text.StoredStatement
import vf.emissary.database.factory.messaging.MessageStatementLinkFactory
import vf.emissary.model.combined.text.MessageStatement
import vf.emissary.model.stored.messaging.MessageStatementLink

/**
  * Used for reading message statements from the database
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
// TODO: Rename to -DbFactory
object MessageStatementFactory 
	extends PlacedStatementDbFactoryLike[MessageStatement, MessageStatementLink]
{
	// IMPLEMENTED	--------------------
	
	override def childFactory = MessageStatementLinkFactory
	
	override def apply(parent: StoredStatement, child: MessageStatementLink): MessageStatement =
		MessageStatement(parent, child)
}

