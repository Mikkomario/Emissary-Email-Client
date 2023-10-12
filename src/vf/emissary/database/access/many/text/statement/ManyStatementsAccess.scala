package vf.emissary.database.access.many.text.statement

import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.ChronoRowFactoryView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.text.StatementFactory
import vf.emissary.model.stored.text.Statement

object ManyStatementsAccess
{
	// NESTED	--------------------
	
	private class ManyStatementsSubView(condition: Condition) extends ManyStatementsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple statements at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait ManyStatementsAccess 
	extends ManyStatementsAccessLike[Statement, ManyStatementsAccess] with ManyRowModelAccess[Statement] 
		with ChronoRowFactoryView[Statement, ManyStatementsAccess]
{
	// IMPLEMENTED	--------------------
	
	override def factory = StatementFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyStatementsAccess = 
		new ManyStatementsAccess.ManyStatementsSubView(mergeCondition(filterCondition))
}

