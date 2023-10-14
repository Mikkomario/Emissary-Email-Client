package vf.emissary.model.combined.messaging

import utopia.flow.view.template.Extender
import vf.emissary.model.partial.messaging.AddressData
import vf.emissary.model.stored.messaging.{Address, AddressName}

/**
  * Connects an email address with its corresponding human-readable names
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
case class NamedAddress(address: Address, names: Vector[AddressName]) extends Extender[AddressData]
{
	// COMPUTED	--------------------
	
	/**
	  * Id of this address in the database
	  */
	def id = address.id
	
	
	// IMPLEMENTED	--------------------
	
	override def wrapped = address.data
}

