package vf.emissary.database.access.many.messaging.message_thread

import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.ChronoRowFactoryView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.MessageThreadFactory
import vf.emissary.model.stored.messaging.MessageThread

object ManyMessageThreadsAccess
{
	// NESTED	--------------------
	
	private class ManyMessageThreadsSubView(condition: Condition) extends ManyMessageThreadsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple message threads at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait ManyMessageThreadsAccess 
	extends ManyMessageThreadsAccessLike[MessageThread, ManyMessageThreadsAccess] 
		with ManyRowModelAccess[MessageThread] 
		with ChronoRowFactoryView[MessageThread, ManyMessageThreadsAccess]
{
	// IMPLEMENTED	--------------------
	
	override def factory = MessageThreadFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyMessageThreadsAccess = 
		new ManyMessageThreadsAccess.ManyMessageThreadsSubView(mergeCondition(filterCondition))
}

