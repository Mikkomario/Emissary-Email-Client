package vf.emissary.model.combined.messaging

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.operator.ordering.CombinedOrdering
import utopia.flow.view.template.Extender
import utopia.vault.store.HasId
import vf.emissary.model.factory.messaging.AddressFactoryWrapper
import vf.emissary.model.partial.messaging.AddressData
import vf.emissary.model.stored.messaging.{Address, AddressName}

object NamedAddress
{
	// ATTRIBUTES	--------------------
	
	implicit val ord: Ordering[NamedAddress] = CombinedOrdering[NamedAddress](
		Ordering.by { a: NamedAddress => a.address.address },
		Ordering.by { a: NamedAddress => a.name.map { _.name } })
	
	
	// OTHER	--------------------
	
	/**
	  * @param address address to wrap
	  * @param names names to attach to this address
	  * @return Combination of the specified address and name
	  */
	def apply(address: Address, names: Seq[AddressName]): NamedAddress = _NamedAddress(address, names)
	
	
	// NESTED	--------------------
	
	/**
	  * @param address address to wrap
	  * @param names names to attach to this address
	  */
	private case class _NamedAddress(address: Address, names: Seq[AddressName]) extends NamedAddress
	{
		// ATTRIBUTES   --------------------
		
		override lazy val name = super.name
		
		
		// IMPLEMENTED	--------------------
		
		override protected def wrap(factory: Address) = copy(address = factory)
	}
}

/**
  * Connects an email address with its corresponding human-readable names
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait NamedAddress 
	extends Extender[AddressData] with HasId[Int] with AddressFactoryWrapper[Address, NamedAddress]
{
	// ABSTRACT	--------------------
	
	/**
	  * Wrapped address
	  */
	def address: Address
	/**
	  * Names that are attached to this address
	  */
	def names: Seq[AddressName]
	
	
	// COMPUTED ------------------------
	
	/**
	 * The preferred (i.e. most recent, preferring self-assigned names) name for this address.
	 * None if this address has no assigned name.
	 */
	def name = names.bestMatch { _.isSelfAssigned }.maxByOption { _.created }
	
	
	// IMPLEMENTED	--------------------
	
	override def wrapped = address.data
	override protected def wrappedFactory = address
	
	/**
	 * Id of this address in the database
	 */
	override def id = address.id
	
	override def toString = name match {
		case Some(name) => name.name
		case None => address.address
	}
}

