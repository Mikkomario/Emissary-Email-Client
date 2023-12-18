package vf.emissary.database.access.many.messaging.pending_thread_reference

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.PendingThreadReferenceFactory
import vf.emissary.database.model.messaging.PendingThreadReferenceModel
import vf.emissary.model.stored.messaging.PendingThreadReference

import java.time.Instant

object ManyPendingThreadReferencesAccess
{
	// NESTED	--------------------
	
	private class ManyPendingThreadReferencesSubView(condition: Condition) 
		extends ManyPendingThreadReferencesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple pending thread references at a time
  * @author Mikko Hilpinen
  * @since 19.10.2023, v0.1
  */
trait ManyPendingThreadReferencesAccess 
	extends ManyRowModelAccess[PendingThreadReference] with FilterableView[ManyPendingThreadReferencesAccess] 
		with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * thread ids of the accessible pending thread references
	  */
	def threadIds(implicit connection: Connection) = pullColumn(model.threadIdColumn).map { v => v.getInt }
	
	/**
	  * referenced message ids of the accessible pending thread references
	  */
	def referencedMessageIds(implicit connection: Connection) = 
		pullColumn(model.referencedMessageIdColumn).flatMap { _.string }
	
	/**
	  * creation times of the accessible pending thread references
	  */
	def creationTimes(implicit connection: Connection) = pullColumn(model.createdColumn)
		.map { v => v.getInstant }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = PendingThreadReferenceModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = PendingThreadReferenceFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyPendingThreadReferencesAccess = 
		new ManyPendingThreadReferencesAccess
			.ManyPendingThreadReferencesSubView(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the creation times of the targeted pending thread references
	  * @param newCreated A new created to assign
	  * @return Whether any pending thread reference was affected
	  */
	def creationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the referenced message ids of the targeted pending thread references
	  * @param newReferencedMessageId A new referenced message id to assign
	  * @return Whether any pending thread reference was affected
	  */
	def referencedMessageIds_=(newReferencedMessageId: String)(implicit connection: Connection) = 
		putColumn(model.referencedMessageIdColumn, newReferencedMessageId)
	
	/**
	  * Updates the thread ids of the targeted pending thread references
	  * @param newThreadId A new thread id to assign
	  * @return Whether any pending thread reference was affected
	  */
	def threadIds_=(newThreadId: Int)(implicit connection: Connection) = 
		putColumn(model.threadIdColumn, newThreadId)
}

