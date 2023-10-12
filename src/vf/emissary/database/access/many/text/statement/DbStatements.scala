package vf.emissary.database.access.many.text.statement

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple statements at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbStatements extends ManyStatementsAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted statements
	  * @return An access point to statements with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbStatementsSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbStatementsSubset(targetIds: Set[Int]) extends ManyStatementsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

