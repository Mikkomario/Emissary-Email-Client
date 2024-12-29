package vf.emissary.database.access.single.messaging.attachment.link.message

import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{FilterableView, ViewFactory}
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.AttachmentMessageLinkDbFactory
import vf.emissary.database.storable.messaging.AttachmentMessageLinkDbModel
import vf.emissary.model.stored.messaging.AttachmentMessageLink

object UniqueAttachmentMessageLinkAccess extends ViewFactory[UniqueAttachmentMessageLinkAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): UniqueAttachmentMessageLinkAccess = 
		_UniqueAttachmentMessageLinkAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _UniqueAttachmentMessageLinkAccess(override val accessCondition: Option[Condition]) 
		extends UniqueAttachmentMessageLinkAccess
}

/**
  * A common trait for access points that return individual and distinct attachment message links.
  * @author Mikko Hilpinen
  * @since 27.12.2024, v1.1
  */
trait UniqueAttachmentMessageLinkAccess 
	extends SingleRowModelAccess[AttachmentMessageLink] 
		with DistinctModelAccess[AttachmentMessageLink, Option[AttachmentMessageLink], Value] 
		with FilterableView[UniqueAttachmentMessageLinkAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the linked attachment. 
	  * None if no attachment message link (or value) was found.
	  */
	def attachmentId(implicit connection: Connection) = pullColumn(model.attachmentId.column).int
	
	/**
	  * Id of the message in which the attachment appears. 
	  * None if no attachment message link (or value) was found.
	  */
	def messageId(implicit connection: Connection) = pullColumn(model.messageId.column).int
	
	/**
	  * Unique id of the accessible attachment message link. None if no attachment message link was 
	  * accessible.
	  */
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = AttachmentMessageLinkDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = AttachmentMessageLinkDbFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): UniqueAttachmentMessageLinkAccess = 
		UniqueAttachmentMessageLinkAccess(condition)
}

