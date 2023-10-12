package vf.emissary.database.access.many.text.statement

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import vf.emissary.database.model.text.StatementModel

import java.time.Instant

/**
  * A common trait for access points which target multiple statements or similar instances at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait ManyStatementsAccessLike[+A, +Repr] extends ManyModelAccess[A] with Indexed with FilterableView[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * delimiter ids of the accessible statements
	  */
	def delimiterIds(implicit connection: Connection) = pullColumn(model.delimiterIdColumn)
		.flatMap { v => v.int }
	
	/**
	  * creation times of the accessible statements
	  */
	def creationTimes(implicit connection: Connection) = pullColumn(model.createdColumn)
		.map { v => v.getInstant }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = StatementModel
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the creation times of the targeted statements
	  * @param newCreated A new created to assign
	  * @return Whether any statement was affected
	  */
	def creationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the delimiter ids of the targeted statements
	  * @param newDelimiterId A new delimiter id to assign
	  * @return Whether any statement was affected
	  */
	def delimiterIds_=(newDelimiterId: Int)(implicit connection: Connection) = 
		putColumn(model.delimiterIdColumn, newDelimiterId)
}

