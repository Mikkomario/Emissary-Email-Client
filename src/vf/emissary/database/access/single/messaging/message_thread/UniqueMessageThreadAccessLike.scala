package vf.emissary.database.access.single.messaging.message_thread

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import vf.emissary.database.model.messaging.MessageThreadModel

import java.time.Instant

/**
  * A common trait for access points which target individual message threads or similar items at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait UniqueMessageThreadAccessLike[+A] 
	extends SingleModelAccess[A] with DistinctModelAccess[A, Option[A], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the address / 
		entity that originated this thread. None if no message thread (or value) was found.
	  */
	def authorId(implicit connection: Connection) = pullColumn(model.authorIdColumn).int
	
	/**
	  * Time when this thread was opened. None if no message thread (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.createdColumn).instant
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
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
	def authorId_=(newAuthorId: Int)(implicit connection: Connection) = 
		putColumn(model.authorIdColumn, newAuthorId)
	
	/**
	  * Updates the creation times of the targeted message threads
	  * @param newCreated A new created to assign
	  * @return Whether any message thread was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
}

