package vf.emissary.model.partial.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.BooleanType
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.mutable.DataType.StringType
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now

import java.time.Instant

object AddressNameData extends FromModelFactoryWithSchema[AddressNameData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("addressId", IntType, Vector("address_id")), 
			PropertyDeclaration("name", StringType, isOptional = true), PropertyDeclaration("created", 
			InstantType, isOptional = true), PropertyDeclaration("isSelfAssigned", BooleanType, 
			Vector("is_self_assigned"), false)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		AddressNameData(valid("addressId").getInt, valid("name").getString, valid("created").getInstant, 
			valid("isSelfAssigned").getBoolean)
}

/**
  * Links a human-readable name to an email address
  * @param addressId Id of the address to which this name corresponds
  * @param name Human-readable name of this entity, if available
  * @param created Time when this link was first documented
  * @param isSelfAssigned Whether this name is used by this person themselves
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
case class AddressNameData(addressId: Int, name: String = "", created: Instant = Now, 
	isSelfAssigned: Boolean = false) 
	extends ModelConvertible
{
	// COMPUTED ------------------------
	
	/**
	 * @return Copy of this data marked as self-assigned
	 */
	def selfAssigned = if (isSelfAssigned) this else copy(isSelfAssigned = true)
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("addressId" -> addressId, "name" -> name, "created" -> created, 
			"isSelfAssigned" -> isSelfAssigned))
	
	override def toString = name
}

