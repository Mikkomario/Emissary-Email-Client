package vf.emissary.database.access.many.messaging.attachment

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.AttachmentFactory
import vf.emissary.database.model.messaging.AttachmentModel
import vf.emissary.model.stored.messaging.Attachment

object ManyAttachmentsAccess
{
	// NESTED	--------------------
	
	private class ManyAttachmentsSubView(condition: Condition) extends ManyAttachmentsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
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
	def originalFileNames(implicit connection: Connection) = 
		pullColumn(model.originalFileNameColumn).flatMap { _.string }
	
	/**
	  * stored file names of the accessible attachments
	  */
	def storedFileNames(implicit connection: Connection) = 
		pullColumn(model.storedFileNameColumn).flatMap { _.string }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = AttachmentModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = AttachmentFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyAttachmentsAccess = 
		new ManyAttachmentsAccess.ManyAttachmentsSubView(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the message ids of the targeted attachments
	  * @param newMessageId A new message id to assign
	  * @return Whether any attachment was affected
	  */
	def messageIds_=(newMessageId: Int)(implicit connection: Connection) = 
		putColumn(model.messageIdColumn, newMessageId)
	
	/**
	  * Updates the original file names of the targeted attachments
	  * @param newOriginalFileName A new original file name to assign
	  * @return Whether any attachment was affected
	  */
	def originalFileNames_=(newOriginalFileName: String)(implicit connection: Connection) = 
		putColumn(model.originalFileNameColumn, newOriginalFileName)
	
	/**
	  * Updates the stored file names of the targeted attachments
	  * @param newStoredFileName A new stored file name to assign
	  * @return Whether any attachment was affected
	  */
	def storedFileNames_=(newStoredFileName: String)(implicit connection: Connection) = 
		putColumn(model.storedFileNameColumn, newStoredFileName)
}

