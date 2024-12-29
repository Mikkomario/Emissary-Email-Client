package vf.emissary.database.access.single.messaging.attachment

import utopia.flow.generic.model.immutable.Value
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.parse.file.FileExtensions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import vf.emissary.database.storable.messaging.{AttachmentDbModel, AttachmentMessageLinkDbModel}

import java.nio.file.Path

/**
  * A common trait for access points which target individual attachments or similar items at a 
  * time
  * @tparam A Type of read (attachments -like) instances
  * @tparam Repr Type of this access point
  * @author Mikko Hilpinen
  * @since 27.12.2024, v1.1
  */
trait UniqueAttachmentAccessLike[+A, +Repr] 
	extends SingleModelAccess[A] with DistinctModelAccess[A, Option[A], Value] with FilterableView[Repr] 
		with Indexed
{
	// COMPUTED	--------------------
	
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
	
	/**
	 * @return DB model for interacting with attachment-message-links
	 */
	protected def messageLinkModel = AttachmentMessageLinkDbModel
	
	
	// OTHER    ---------------------
	
	/**
	 * @param messageId Id of the targeted message
	 * @param connection Implicit DB connection
	 * @return Whether this attachment appears within the specified message
	 */
	def isLinkedToMessage(messageId: Int)(implicit connection: Connection) =
		exists(messageLinkModel.messageId <=> messageId, messageLinkModel.table)
}

