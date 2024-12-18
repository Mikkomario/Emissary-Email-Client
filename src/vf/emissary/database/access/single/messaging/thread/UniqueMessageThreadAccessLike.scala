package vf.emissary.database.access.single.messaging.thread

import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import vf.emissary.database.storable.messaging.MessageThreadDbModel

/**
  * A common trait for access points which target individual message threads or similar items at a time
  * @tparam A Type of read (message threads -like) instances
  * @tparam Repr Type of this access point
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait UniqueMessageThreadAccessLike[+A, +Repr] 
	extends SingleModelAccess[A] with DistinctModelAccess[A, Option[A], Value] with FilterableView[Repr] 
		with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Time when this thread was opened. 
	  * None if no message thread (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.created.column).instant
	
	/**
	  * Unique id of the accessible message thread. None if no message thread was accessible.
	  */
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = MessageThreadDbModel
}

