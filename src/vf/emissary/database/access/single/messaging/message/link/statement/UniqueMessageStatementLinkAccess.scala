package vf.emissary.database.access.single.messaging.message.link.statement

import utopia.logos.database.access.single.text.statement.placement.UniqueStatementPlacementAccessLike
import utopia.vault.database.Connection
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.MessageStatementLinkDbFactory
import vf.emissary.database.storable.messaging.MessageStatementLinkDbModel
import vf.emissary.model.stored.messaging.MessageStatementLink

object UniqueMessageStatementLinkAccess extends ViewFactory[UniqueMessageStatementLinkAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): UniqueMessageStatementLinkAccess = 
		_UniqueMessageStatementLinkAccess(condition)
	
	
	// NESTED	--------------------
	
	private case class _UniqueMessageStatementLinkAccess(override val condition: Condition)
		extends UniqueMessageStatementLinkAccess
}

/**
  * A common trait for access points that return individual and distinct message statement links.
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait UniqueMessageStatementLinkAccess 
	extends UniqueStatementPlacementAccessLike[MessageStatementLink, UniqueMessageStatementLinkAccess]
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the message where the statement was made. 
	  * None if no message statement link (or value) was found.
	  */
	def messageId(implicit connection: Connection) = parentId
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageStatementLinkDbFactory
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	override protected def model = MessageStatementLinkDbModel
	
	override protected def self = this
	
	override def apply(condition: Condition): UniqueMessageStatementLinkAccess = 
		UniqueMessageStatementLinkAccess(condition)
}

