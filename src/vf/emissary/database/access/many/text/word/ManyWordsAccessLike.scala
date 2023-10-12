package vf.emissary.database.access.many.text.word

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import vf.emissary.database.model.text.WordModel

import java.time.Instant

/**
  * A common trait for access points which target multiple words or similar instances at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait ManyWordsAccessLike[+A, +Repr] extends ManyModelAccess[A] with Indexed with FilterableView[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * text of the accessible words
	  */
	def text(implicit connection: Connection) = pullColumn(model.textColumn).flatMap { _.string }
	
	/**
	  * creation times of the accessible words
	  */
	def creationTimes(implicit connection: Connection) = pullColumn(model.createdColumn)
		.map { v => v.getInstant }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = WordModel
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the creation times of the targeted words
	  * @param newCreated A new created to assign
	  * @return Whether any word was affected
	  */
	def creationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the text of the targeted words
	  * @param newText A new text to assign
	  * @return Whether any word was affected
	  */
	def text_=(newText: String)(implicit connection: Connection) = putColumn(model.textColumn, newText)
}

