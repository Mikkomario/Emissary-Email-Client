package vf.emissary.database.access.many.messaging.pending_reply_reference

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.PendingReplyReferenceFactory
import vf.emissary.database.model.messaging.PendingReplyReferenceModel
import vf.emissary.model.stored.messaging.PendingReplyReference

import java.time.Instant

object ManyPendingReplyReferencesAccess
{
	// NESTED	--------------------
	
	private class ManyPendingReplyReferencesSubView(condition: Condition)
		 extends ManyPendingReplyReferencesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple pending reply references at a time
  * @author Mikko Hilpinen
  * @since 19.10.2023, v0.1
  */
trait ManyPendingReplyReferencesAccess 
	extends ManyRowModelAccess[PendingReplyReference] with FilterableView[ManyPendingReplyReferencesAccess] 
		with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * message ids of the accessible pending reply references
	  */
	def messageIds(implicit connection: Connection) = pullColumn(model.messageIdColumn).map { v => v.getInt }
	
	/**
	  * referenced message ids of the accessible pending reply references
	  */
	def referencedMessageIds(implicit connection: Connection) = 
		pullColumn(model.referencedMessageIdColumn).flatMap { _.string }
	
	/**
	  * creation times of the accessible pending reply references
	  */
	def creationTimes(implicit connection: Connection) = pullColumn(model.createdColumn)
		.map { v => v.getInstant }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = PendingReplyReferenceModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = PendingReplyReferenceFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyPendingReplyReferencesAccess = 
		new ManyPendingReplyReferencesAccess
			.ManyPendingReplyReferencesSubView(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the creation times of the targeted pending reply references
	  * @param newCreated A new created to assign
	  * @return Whether any pending reply reference was affected
	  */
	def creationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the message ids of the targeted pending reply references
	  * @param newMessageId A new message id to assign
	  * @return Whether any pending reply reference was affected
	  */
	def messageIds_=(newMessageId: Int)(implicit connection: Connection) = 
		putColumn(model.messageIdColumn, newMessageId)
	
	/**
	  * Updates the referenced message ids of the targeted pending reply references
	  * @param newReferencedMessageId A new referenced message id to assign
	  * @return Whether any pending reply reference was affected
	  */
	def referencedMessageIds_=(newReferencedMessageId: String)(implicit connection: Connection) = 
		putColumn(model.referencedMessageIdColumn, newReferencedMessageId)
}

