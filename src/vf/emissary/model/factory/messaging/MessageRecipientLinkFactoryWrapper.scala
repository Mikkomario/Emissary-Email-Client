package vf.emissary.model.factory.messaging

import utopia.flow.util.Mutate
import vf.emissary.model.enumeration.RecipientType

/**
  * Common
  * 
	 trait for classes that implement MessageRecipientLinkFactory by wrapping a MessageRecipientLinkFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait MessageRecipientLinkFactoryWrapper[A <: MessageRecipientLinkFactory[A], +Repr] 
	extends MessageRecipientLinkFactory[Repr]
{
	// ABSTRACT	--------------------
	
	/**
	  * The factory wrapped by this instance
	  */
	protected def wrappedFactory: A
	
	/**
	  * Mutates this item by wrapping a mutated instance
	  * @param factory The new factory instance to wrap
	  * @return Copy of this item with the specified wrapped factory
	  */
	protected def wrap(factory: A): Repr
	
	
	// IMPLEMENTED	--------------------
	
	override def withMessageId(messageId: Int) = mapWrapped { _.withMessageId(messageId) }
	
	override def withRecipientId(recipientId: Int) = mapWrapped { _.withRecipientId(recipientId) }
	
	override def withRole(role: RecipientType) = mapWrapped { _.withRole(role) }
	
	
	// OTHER	--------------------
	
	/**
	  * Modifies this item by mutating the wrapped factory instance
	  * @param f A function for mutating the wrapped factory instance
	  * @return Copy of this item with a mutated wrapped factory
	  */
	protected def mapWrapped(f: Mutate[A]) = wrap(f(wrappedFactory))
}

