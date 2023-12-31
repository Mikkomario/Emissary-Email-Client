package vf.emissary.database.access.many.text.word

import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.sql.Condition
import vf.emissary.database.factory.text.WordFactory
import vf.emissary.model.stored.text.Word

object ManyWordsAccess
{
	// NESTED	--------------------
	
	private class ManyWordsSubView(condition: Condition) extends ManyWordsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple words at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait ManyWordsAccess extends ManyWordsAccessLike[Word, ManyWordsAccess] with ManyRowModelAccess[Word]
{
	// COMPUTED ------------------------
	
	/**
	 * @param connection Implicit DB Connection
	 * @return All accessible word ids mapped to their string values
	 */
	def toMap(implicit connection: Connection) =
		pullColumnMap(model.textColumn, index).map { case (text, id) => text.getString -> id.getInt }
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = WordFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyWordsAccess = 
		new ManyWordsAccess.ManyWordsSubView(mergeCondition(filterCondition))
}

