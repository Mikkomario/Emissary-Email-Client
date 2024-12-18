package vf.emissary.database.access.many.messaging.message.link.statement

import utopia.flow.collection.immutable.IntSet
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.emissary.database.access.many.text.placement.statement.ManyStatementPlacementsAccessLike
import vf.emissary.database.factory.messaging.MessageStatementLinkDbFactory
import vf.emissary.database.storable.messaging.MessageStatementLinkDbModel
import vf.emissary.model.stored.messaging.MessageStatementLink

object ManyMessageStatementLinksAccess extends ViewFactory[ManyMessageStatementLinksAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyMessageStatementLinksAccess = 
		_ManyMessageStatementLinksAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyMessageStatementLinksAccess(override val accessCondition: Option[Condition]) 
		extends ManyMessageStatementLinksAccess
}

/**
  * A common trait for access points which target multiple message statement links at a time
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait ManyMessageStatementLinksAccess 
	extends ManyStatementPlacementsAccessLike[MessageStatementLink, ManyMessageStatementLinksAccess] 
		with ManyRowModelAccess[MessageStatementLink]
{
	// COMPUTED	--------------------
	
	/**
	  * message ids of the accessible message statement links
	  */
	def messageIds(implicit connection: Connection) = parentIds
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageStatementLinkDbFactory
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	override protected def model = MessageStatementLinkDbModel
	
	override protected def self = this
	
	override def apply(condition: Condition): ManyMessageStatementLinksAccess = 
		ManyMessageStatementLinksAccess(condition)
	
	
	// OTHER	--------------------
	
	/**
	  * @param messageId message id to target
	  * @return Copy of this access point that only includes message statement links 
		with the specified message id
	  */
	def withinMessage(messageId: Int) = filter(model.messageId.column <=> messageId)
	
	/**
	  * @param messageIds Targeted message ids
	  * @return
	  * 
		 Copy of this access point that only includes message statement links where message id is within the specified value set
	  */
	def withinMessages(messageIds: IterableOnce[Int]) = filter(model
		.messageId.column.in(IntSet.from(messageIds)))
}

