package vf.emissary.database.access.many.messaging.subject

import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import vf.emissary.database.storable.messaging.SubjectDbModel

/**
  * A common trait for access points which target multiple subjects or similar instances at a time
  * @tparam A Type of read (subjects -like) instances
  * @tparam Repr Type of this access point
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait ManySubjectsAccessLike[+A, +Repr] extends ManyModelAccess[A] with Indexed with FilterableView[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * creation times of the accessible subjects
	  */
	def creationTimes(implicit connection: Connection) = 
		pullColumn(model.created.column).map { v => v.getInstant }
	/**
	  * Unique ids of the accessible subjects
	  */
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = SubjectDbModel
}

