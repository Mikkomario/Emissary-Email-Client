package vf.emissary.database.access.many.text.statement

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple message statements at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbMessageStatements extends ManyMessageStatementsAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted message statements
	  * @return An access point to message statements with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbMessageStatementsSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbMessageStatementsSubset(targetIds: Set[Int]) extends ManyMessageStatementsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(index in targetIds)
	}
}

