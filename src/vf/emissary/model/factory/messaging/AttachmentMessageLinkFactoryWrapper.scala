package vf.emissary.model.factory.messaging

import utopia.flow.util.Mutate

/**
  * Common trait for classes that implement AttachmentMessageLinkFactory by wrapping a 
  * AttachmentMessageLinkFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 27.12.2024, v1.1
  */
trait AttachmentMessageLinkFactoryWrapper[A <: AttachmentMessageLinkFactory[A], +Repr] 
	extends AttachmentMessageLinkFactory[Repr]
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
	
	override def withAttachmentId(attachmentId: Int) = mapWrapped { _.withAttachmentId(attachmentId) }
	
	override def withMessageId(messageId: Int) = mapWrapped { _.withMessageId(messageId) }
	
	
	// OTHER	--------------------
	
	/**
	  * Modifies this item by mutating the wrapped factory instance
	  * @param f A function for mutating the wrapped factory instance
	  * @return Copy of this item with a mutated wrapped factory
	  */
	protected def mapWrapped(f: Mutate[A]) = wrap(f(wrappedFactory))
}

