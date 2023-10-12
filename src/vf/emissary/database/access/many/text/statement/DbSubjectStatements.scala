package vf.emissary.database.access.many.text.statement

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple subject statements at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbSubjectStatements extends ManySubjectStatementsAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted subject statements
	  * @return An access point to subject statements with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbSubjectStatementsSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbSubjectStatementsSubset(targetIds: Set[Int]) extends ManySubjectStatementsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

