package vf.emissary.database.access.many.messaging.message_thread

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import vf.emissary.database.model.messaging.MessageThreadModel

import java.time.Instant

/**
  * A common trait for access points which target multiple message threads or similar instances at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait ManyMessageThreadsAccessLike[+A, +Repr] 
	extends ManyModelAccess[A] with Indexed with FilterableView[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * author ids of the accessible message threads
	  */
	def authorIds(implicit connection: Connection) = pullColumn(model.authorIdColumn).map { v => v.getInt }
	
	/**
	  * creation times of the accessible message threads
	  */
	def creationTimes(implicit connection: Connection) = pullColumn(model.createdColumn)
		.map { v => v.getInstant }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = MessageThreadModel
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the author ids of the targeted message threads
	  * @param newAuthorId A new author id to assign
	  * @return Whether any message thread was affected
	  */
	def authorIds_=(newAuthorId: Int)(implicit connection: Connection) = 
		putColumn(model.authorIdColumn, newAuthorId)
	
	/**
	  * Updates the creation times of the targeted message threads
	  * @param newCreated A new created to assign
	  * @return Whether any message thread was affected
	  */
	def creationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
}

