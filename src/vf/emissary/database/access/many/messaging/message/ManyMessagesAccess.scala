package vf.emissary.database.access.many.messaging.message

import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.ChronoRowFactoryView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.MessageFactory
import vf.emissary.model.stored.messaging.Message

object ManyMessagesAccess
{
	// NESTED	--------------------
	
	private class ManyMessagesSubView(condition: Condition) extends ManyMessagesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple messages at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
trait ManyMessagesAccess 
	extends ManyMessagesAccessLike[Message, ManyMessagesAccess] with ManyRowModelAccess[Message] 
		with ChronoRowFactoryView[Message, ManyMessagesAccess]
{
	// IMPLEMENTED	--------------------
	
	override def factory = MessageFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyMessagesAccess = 
		new ManyMessagesAccess.ManyMessagesSubView(mergeCondition(filterCondition))
}

