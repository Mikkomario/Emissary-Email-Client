package vf.emissary.database.access.many.text.delimiter

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple delimiters at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbDelimiters extends ManyDelimitersAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted delimiters
	  * @return An access point to delimiters with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbDelimitersSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbDelimitersSubset(targetIds: Set[Int]) extends ManyDelimitersAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

