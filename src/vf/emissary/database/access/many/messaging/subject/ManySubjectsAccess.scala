package vf.emissary.database.access.many.messaging.subject

import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.SubjectFactory
import vf.emissary.model.stored.messaging.Subject

object ManySubjectsAccess
{
	// NESTED	--------------------
	
	private class ManySubjectsSubView(condition: Condition) extends ManySubjectsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple subjects at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait ManySubjectsAccess 
	extends ManySubjectsAccessLike[Subject, ManySubjectsAccess] with ManyRowModelAccess[Subject]
{
	// IMPLEMENTED	--------------------
	
	override def factory = SubjectFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManySubjectsAccess = 
		new ManySubjectsAccess.ManySubjectsSubView(mergeCondition(filterCondition))
}

