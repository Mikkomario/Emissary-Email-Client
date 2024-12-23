package vf.emissary.database.access.single.messaging.attachment

import utopia.flow.generic.model.immutable.Value
import utopia.flow.parse.file.FileExtensions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{FilterableView, ViewFactory}
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.AttachmentDbFactory
import vf.emissary.database.storable.messaging.AttachmentDbModel
import vf.emissary.model.stored.messaging.Attachment

import java.nio.file.Path

object UniqueAttachmentAccess extends ViewFactory[UniqueAttachmentAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): UniqueAttachmentAccess = _UniqueAttachmentAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _UniqueAttachmentAccess(override val accessCondition: Option[Condition]) 
		extends UniqueAttachmentAccess
}

/**
  * A common trait for access points that return individual and distinct attachments.
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
trait UniqueAttachmentAccess 
	extends SingleRowModelAccess[Attachment] with DistinctModelAccess[Attachment, Option[Attachment], Value] 
		with FilterableView[UniqueAttachmentAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the message to which this file is attached. 
	  * None if no attachment (or value) was found.
	  */
	def messageId(implicit connection: Connection) = pullColumn(model.messageId.column).int
	/**
	  * Name of the attached file, as appears on the file system. 
	  * None if no attachment (or value) was found.
	  */
	def relativePath(implicit connection: Connection) = 
		Some(pullColumn(model.relativePath.column).getString: Path)
	/**
	  * Size of this attachment in bytes. 
	  * None if no attachment (or value) was found.
	  */
	def size(implicit connection: Connection) = pullColumn(model.size.column).long
	/**
	  * Unique id of the accessible attachment. None if no attachment was accessible.
	  */
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = AttachmentDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = AttachmentDbFactory
	override protected def self = this
	
	override def apply(condition: Condition): UniqueAttachmentAccess = UniqueAttachmentAccess(condition)
}

