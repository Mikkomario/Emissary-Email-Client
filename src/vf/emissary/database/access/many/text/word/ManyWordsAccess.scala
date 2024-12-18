package vf.emissary.database.access.many.text.word

import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.emissary.database.factory.text.WordFactory
import vf.emissary.model.stored.text.Word

@deprecated("Moved to Logos", "v1.1")
object ManyWordsAccess extends ViewFactory[ManyWordsAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyWordsAccess = _ManyWordsAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyWordsAccess(override val accessCondition: Option[Condition]) extends ManyWordsAccess
}

/**
  * A common trait for access points which target multiple words at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
@deprecated("Moved to Logos", "v1.1")
trait ManyWordsAccess extends ManyWordsAccessLike[Word, ManyWordsAccess] with ManyRowModelAccess[Word]
{
	// COMPUTED	--------------------
	
	/**
	  * All accessible word ids mapped to their string values
	  * @param connection Implicit DB Connection
	  */
	def toMap(implicit connection: Connection) = 
		pullColumnMap(model.textColumn, index).map { case (text, id) => text.getString -> id.getInt }
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = WordFactory
	override protected def self = this
	
	override def apply(condition: Condition): ManyWordsAccess = ManyWordsAccess(condition)
}

