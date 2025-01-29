package vf.emissary.model.factory.messaging

import utopia.flow.util.Mutate

import java.time.Instant

/**
  * Common trait for classes that implement EmailServiceUserFactory by wrapping a 
  * EmailServiceUserFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 22.12.2024, v1.1
  */
trait EmailServiceUserFactoryWrapper[A <: EmailServiceUserFactory[A], +Repr] 
	extends EmailServiceUserFactory[Repr]
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
	
	override def withAddressId(addressId: Int) = mapWrapped { _.withAddressId(addressId) }
	
	override def withCreated(created: Instant) = mapWrapped { _.withCreated(created) }
	
	override def withPassword(password: String) = mapWrapped { _.withPassword(password) }
	
	override def withServiceId(serviceId: Int) = mapWrapped { _.withServiceId(serviceId) }
	
	
	// OTHER	--------------------
	
	/**
	  * Modifies this item by mutating the wrapped factory instance
	  * @param f A function for mutating the wrapped factory instance
	  * @return Copy of this item with a mutated wrapped factory
	  */
	protected def mapWrapped(f: Mutate[A]) = wrap(f(wrappedFactory))
}

