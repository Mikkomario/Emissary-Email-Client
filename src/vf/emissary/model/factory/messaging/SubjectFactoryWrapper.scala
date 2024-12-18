package vf.emissary.model.factory.messaging

import utopia.flow.util.Mutate

import java.time.Instant

/**
  * Common trait for classes that implement SubjectFactory by wrapping a SubjectFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait SubjectFactoryWrapper[A <: SubjectFactory[A], +Repr] extends SubjectFactory[Repr]
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
	
	override def withCreated(created: Instant) = mapWrapped { _.withCreated(created) }
	
	
	// OTHER	--------------------
	
	/**
	  * Modifies this item by mutating the wrapped factory instance
	  * @param f A function for mutating the wrapped factory instance
	  * @return Copy of this item with a mutated wrapped factory
	  */
	protected def mapWrapped(f: Mutate[A]) = wrap(f(wrappedFactory))
}

