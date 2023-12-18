package vf.emissary.model.combined.messaging

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.operator.ordering.CombinedOrdering
import utopia.flow.view.template.Extender
import vf.emissary.model.partial.messaging.AddressData
import vf.emissary.model.stored.messaging.{Address, AddressName}

object NamedAddress
{
	implicit val ord: Ordering[NamedAddress] = CombinedOrdering[NamedAddress](
		Ordering.by { a: NamedAddress => a.address.address },
		Ordering.by { a: NamedAddress => a.name.map { _.name } }
	)
}

/**
  * Connects an email address with its corresponding human-readable names
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
case class NamedAddress(address: Address, names: Vector[AddressName]) extends Extender[AddressData]
{
	// ATTRIBUTES   ----------------
	
	/**
	 * The preferred (i.e. most recent, preferring self-assigned names) name for this address.
	 * None if this address has no assigned name.
	 */
	lazy val name = names.bestMatch { _.isSelfAssigned }.maxByOption { _.created }
	
	
	// COMPUTED	--------------------
	
	/**
	  * Id of this address in the database
	  */
	def id = address.id
	
	
	// IMPLEMENTED	--------------------
	
	override def wrapped = address.data
	
	override def toString = name match {
		case Some(name) => name.name
		case None => address.address
	}
}

