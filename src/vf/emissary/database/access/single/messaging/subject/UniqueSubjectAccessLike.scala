package vf.emissary.database.access.single.messaging.subject

import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import vf.emissary.database.storable.messaging.SubjectDbModel

/**
  * A common trait for access points which target individual subjects or similar items at a time
  * @tparam A Type of read (subjects -like) instances
  * @tparam Repr Type of this access point
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait UniqueSubjectAccessLike[+A, +Repr] 
	extends SingleModelAccess[A] with DistinctModelAccess[A, Option[A], Value] with FilterableView[Repr] 
		with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Time when this subject was first used. 
	  * None if no subject (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.created.column).instant
	/**
	  * Unique id of the accessible subject. None if no subject was accessible.
	  */
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = SubjectDbModel
}

