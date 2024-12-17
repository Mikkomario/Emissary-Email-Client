package vf.emissary.database.access.many.messaging.attachment

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{FilterableView, ViewFactory}
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.AttachmentFactory
import vf.emissary.database.model.messaging.AttachmentModel
import vf.emissary.model.stored.messaging.Attachment

object ManyAttachmentsAccess extends ViewFactory[ManyAttachmentsAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyAttachmentsAccess = _ManyAttachmentsAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyAttachmentsAccess(override val accessCondition: Option[Condition]) 
		extends ManyAttachmentsAccess
}

/**
  * A common trait for access points which target multiple attachments at a time
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
trait ManyAttachmentsAccess 
	extends ManyRowModelAccess[Attachment] with FilterableView[ManyAttachmentsAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * message ids of the accessible attachments
	  */
	def messageIds(implicit connection: Connection) = pullColumn(model.messageIdColumn).map { v => v.getInt }
	
	/**
	  * original file names of the accessible attachments
	  */
	def fileNames(implicit connection: Connection) = pullColumn(model.fileNameColumn).flatMap { _.string }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = AttachmentModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = AttachmentFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): ManyAttachmentsAccess = ManyAttachmentsAccess(condition)
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the original file names of the targeted attachments
	  * @param newFileName A new file name to assign
	  * @return Whether any attachment was affected
	  */
	def fileNames_=(newFileName: String)(implicit connection: Connection) = 
		putColumn(model.fileNameColumn, newFileName)
	
	/**
	  * @param messageId Id of the targeted message
	  * @return Access to attachments within that message
	  */
	def inMessage(messageId: Int) = filter(model.withMessageId(messageId).toCondition)
	
	/**
	  * @param messageIds Ids of the targeted messages
	  * @return Access to attachments in those messages
	  */
	def inMessages(messageIds: Iterable[Int]) = filter(model.messageIdColumn.in(messageIds))
	
	/**
	  * Updates the message ids of the targeted attachments
	  * @param newMessageId A new message id to assign
	  * @return Whether any attachment was affected
	  */
	def messageIds_=(newMessageId: Int)(implicit connection: Connection) = 
		putColumn(model.messageIdColumn, newMessageId)
}

