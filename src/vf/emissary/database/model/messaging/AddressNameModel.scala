package vf.emissary.database.model.messaging

import com.vdurmont.emoji.EmojiParser
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.flow.util.StringExtensions._
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.emissary.database.factory.messaging.AddressNameFactory
import vf.emissary.model.partial.messaging.AddressNameData
import vf.emissary.model.stored.messaging.AddressName

import java.time.Instant

/**
  * Used for constructing AddressNameModel instances and for inserting address names to the database
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
object AddressNameModel extends DataInserter[AddressNameModel, AddressName, AddressNameData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains address name address id
	  */
	val addressIdAttName = "addressId"
	
	/**
	  * Name of the property that contains address name name
	  */
	val nameAttName = "name"
	
	/**
	  * Name of the property that contains address name created
	  */
	val createdAttName = "created"
	
	/**
	  * Name of the property that contains address name is self assigned
	  */
	val isSelfAssignedAttName = "isSelfAssigned"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains address name address id
	  */
	def addressIdColumn = table(addressIdAttName)
	
	/**
	  * Column that contains address name name
	  */
	def nameColumn = table(nameAttName)
	
	/**
	  * Column that contains address name created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * Column that contains address name is self assigned
	  */
	def isSelfAssignedColumn = table(isSelfAssignedAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = AddressNameFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: AddressNameData) = 
		apply(None, Some(data.addressId), data.name, Some(data.created), Some(data.isSelfAssigned))
	
	override protected def complete(id: Value, data: AddressNameData) = AddressName(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param addressId Id of the address to which this name corresponds
	  * @return A model containing only the specified address id
	  */
	def withAddressId(addressId: Int) = apply(addressId = Some(addressId))
	
	/**
	  * @param created Time when this link was first documented
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param id A address name id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param isSelfAssigned Whether this name is used by this person themselves
	  * @return A model containing only the specified is self assigned
	  */
	def withIsSelfAssigned(isSelfAssigned: Boolean) = apply(isSelfAssigned = Some(isSelfAssigned))
	
	/**
	  * @param name Human-readable name of this entity, if available
	  * @return A model containing only the specified name
	  */
	def withName(name: String) = apply(name = name)
}

/**
  * Used for interacting with AddressNames in the database
  * @param id address name database id
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
case class AddressNameModel(id: Option[Int] = None, addressId: Option[Int] = None, name: String = "", 
	created: Option[Instant] = None, isSelfAssigned: Option[Boolean] = None) 
	extends StorableWithFactory[AddressName]
{
	// IMPLEMENTED	--------------------
	
	override def factory = AddressNameModel.factory
	
	override def valueProperties = {
		import AddressNameModel._
		// Parses potential emoji content
		val nonEmojiName = name.mapIfNotEmpty(EmojiParser.parseToAliases)
		Vector("id" -> id, addressIdAttName -> addressId, nameAttName -> nonEmojiName, createdAttName -> created,
			isSelfAssignedAttName -> isSelfAssigned)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param addressId Id of the address to which this name corresponds
	  * @return A new copy of this model with the specified address id
	  */
	def withAddressId(addressId: Int) = copy(addressId = Some(addressId))
	
	/**
	  * @param created Time when this link was first documented
	  * @return A new copy of this model with the specified created
	  */
	def withCreated(created: Instant) = copy(created = Some(created))
	
	/**
	  * @param isSelfAssigned Whether this name is used by this person themselves
	  * @return A new copy of this model with the specified is self assigned
	  */
	def withIsSelfAssigned(isSelfAssigned: Boolean) = copy(isSelfAssigned = Some(isSelfAssigned))
	
	/**
	  * @param name Human-readable name of this entity, if available
	  * @return A new copy of this model with the specified name
	  */
	def withName(name: String) = copy(name = name)
}

