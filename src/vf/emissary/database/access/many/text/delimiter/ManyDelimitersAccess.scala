package vf.emissary.database.access.many.text.delimiter

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.text.DelimiterFactory
import vf.emissary.database.model.text.DelimiterModel
import vf.emissary.model.stored.text.Delimiter

import java.time.Instant

object ManyDelimitersAccess
{
	// NESTED	--------------------
	
	private class ManyDelimitersSubView(condition: Condition) extends ManyDelimitersAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple delimiters at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait ManyDelimitersAccess 
	extends ManyRowModelAccess[Delimiter] with FilterableView[ManyDelimitersAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * text of the accessible delimiters
	  */
	def text(implicit connection: Connection) = pullColumn(model.textColumn).flatMap { _.string }
	
	/**
	  * creation times of the accessible delimiters
	  */
	def creationTimes(implicit connection: Connection) = pullColumn(model.createdColumn)
		.map { v => v.getInstant }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = DelimiterModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = DelimiterFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyDelimitersAccess = 
		new ManyDelimitersAccess.ManyDelimitersSubView(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the creation times of the targeted delimiters
	  * @param newCreated A new created to assign
	  * @return Whether any delimiter was affected
	  */
	def creationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the text of the targeted delimiters
	  * @param newText A new text to assign
	  * @return Whether any delimiter was affected
	  */
	def text_=(newText: String)(implicit connection: Connection) = putColumn(model.textColumn, newText)
}

