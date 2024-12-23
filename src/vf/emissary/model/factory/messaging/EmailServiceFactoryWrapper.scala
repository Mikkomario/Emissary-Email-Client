package vf.emissary.model.factory.messaging

import utopia.flow.util.Mutate

import java.time.Instant

/**
  * Common trait for classes that implement EmailServiceFactory by wrapping a EmailServiceFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 22.12.2024, v1.1
  */
trait EmailServiceFactoryWrapper[A <: EmailServiceFactory[A], +Repr] extends EmailServiceFactory[Repr]
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
	
	override def withAddress(address: String) = mapWrapped { _.withAddress(address) }
	
	override def withCreated(created: Instant) = mapWrapped { _.withCreated(created) }
	
	override def withName(name: String) = mapWrapped { _.withName(name) }
	
	
	// OTHER	--------------------
	
	/**
	  * Modifies this item by mutating the wrapped factory instance
	  * @param f A function for mutating the wrapped factory instance
	  * @return Copy of this item with a mutated wrapped factory
	  */
	protected def mapWrapped(f: Mutate[A]) = wrap(f(wrappedFactory))
}

