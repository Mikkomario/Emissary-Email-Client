package vf.emissary.database.access.many.text.statement

import utopia.logos.database.access.many.text.statement.ManyPlacedStatementsAccessLike
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.emissary.database.factory.text.MessageStatementFactory
import vf.emissary.database.storable.messaging.MessageStatementLinkDbModel
import vf.emissary.model.combined.text.MessageStatement

object ManyMessageStatementsAccess extends ViewFactory[ManyMessageStatementsAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyMessageStatementsAccess = 
		_ManyMessageStatementsAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyMessageStatementsAccess(override val accessCondition: Option[Condition]) 
		extends ManyMessageStatementsAccess
}

/**
  * A common trait for access points that return multiple message statements at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023
  */
trait ManyMessageStatementsAccess 
	extends ManyPlacedStatementsAccessLike[MessageStatement, ManyMessageStatementsAccess]
		with ManyRowModelAccess[MessageStatement]
{
	// COMPUTED	--------------------
	
	/**
	  * message ids of the accessible message statement links
	  */
	def messageLinkMessageIds(implicit connection: Connection) = 
		pullColumn(messageLinkModel.messageId).map { v => v.getInt }
	/**
	  * statement ids of the accessible message statement links
	  */
	def messageLinkStatementIds(implicit connection: Connection) = 
		pullColumn(messageLinkModel.statementId).map { v => v.getInt }
	/**
	  * order indexs of the accessible message statement links
	  */
	def messageLinkOrderIndices(implicit connection: Connection) = 
		pullColumn(messageLinkModel.orderIndex).map { v => v.getInt }
	
	/**
	  * Model (factory) used for interacting the message statement links associated 
	  * with this message statement
	  */
	protected def messageLinkModel = MessageStatementLinkDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	override def factory = MessageStatementFactory
	override protected def placementModel = messageLinkModel
	
	
	// OTHER	--------------------
	
	def apply(condition: Condition): ManyMessageStatementsAccess = ManyMessageStatementsAccess(condition)
	
	/**
	  * @param messageIds Ids of the targeted messages
	  * @return Access to statements made within the specified messages
	  */
	def inMessages(messageIds: Iterable[Int]) = withinTexts(messageIds)
}

