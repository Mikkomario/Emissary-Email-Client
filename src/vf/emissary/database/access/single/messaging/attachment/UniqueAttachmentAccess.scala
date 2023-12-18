package vf.emissary.database.access.single.messaging.attachment

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.AttachmentFactory
import vf.emissary.database.model.messaging.AttachmentModel
import vf.emissary.model.stored.messaging.Attachment

object UniqueAttachmentAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueAttachmentAccess = new _UniqueAttachmentAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueAttachmentAccess(condition: Condition) extends UniqueAttachmentAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct attachments.
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
trait UniqueAttachmentAccess 
	extends SingleRowModelAccess[Attachment] with FilterableView[UniqueAttachmentAccess] 
		with DistinctModelAccess[Attachment, Option[Attachment], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the message to which this file is attached. None if no attachment (or value) was found.
	  */
	def messageId(implicit connection: Connection) = pullColumn(model.messageIdColumn).int
	
	/**
	  * Name of the attached file, as it was originally sent. None if no attachment (or value) was found.
	  */
	def fileName(implicit connection: Connection) = pullColumn(model.fileNameColumn).getString
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = AttachmentModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = AttachmentFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueAttachmentAccess = 
		new UniqueAttachmentAccess._UniqueAttachmentAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the message ids of the targeted attachments
	  * @param newMessageId A new message id to assign
	  * @return Whether any attachment was affected
	  */
	def messageId_=(newMessageId: Int)(implicit connection: Connection) = 
		putColumn(model.messageIdColumn, newMessageId)
	
	/**
	  * Updates the original file names of the targeted attachments
	  * @param newFileName A new file name to assign
	  * @return Whether any attachment was affected
	  */
	def fileName_=(newFileName: String)(implicit connection: Connection) =
		putColumn(model.fileNameColumn, newFileName)
}

