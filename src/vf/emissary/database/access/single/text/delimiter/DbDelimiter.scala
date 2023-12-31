package vf.emissary.database.access.single.text.delimiter

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.text.DelimiterFactory
import vf.emissary.database.model.text.DelimiterModel
import vf.emissary.model.stored.text.Delimiter

/**
  * Used for accessing individual delimiters
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbDelimiter extends SingleRowModelAccess[Delimiter] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = DelimiterModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = DelimiterFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted delimiter
	  * @return An access point to that delimiter
	  */
	def apply(id: Int) = DbSingleDelimiter(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique delimiters.
	  * @return An access point to the delimiter that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = UniqueDelimiterAccess(mergeCondition(condition))
}

