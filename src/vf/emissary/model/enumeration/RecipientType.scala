package vf.emissary.model.enumeration

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.flow.generic.model.template.ValueConvertible

import scala.language.implicitConversions

/**
  * Common trait for all recipient type values
  * @author Mikko Hilpinen
  * @since 15.10.2023, v0.1
  */
sealed trait RecipientType extends ValueConvertible
{
	// ABSTRACT	--------------------
	
	/**
	  * id used to represent this recipient type in database and json
	  */
	def id: Int
	
	
	// IMPLEMENTED	--------------------
	
	override def toValue = id
}

object RecipientType
{
	import javax.mail.Message
	
	// TYPES    ------------------------
	
	type JRecipientType = Message.RecipientType
	
	
	// ATTRIBUTES	--------------------
	
	/**
	  * All available recipient type values
	  */
	val values: Vector[RecipientType] = Vector(Primary, Copy, HiddenCopy)
	
	
	// COMPUTED	--------------------
	
	/**
	  * The default recipient type (i.e. primary)
	  */
	def default = Primary
	
	
	// OTHER	--------------------
	
	/**
	 * Implicitly converts from a java-based recipient-type value
	 * @param recipientType A recipient type from javax.mail
	 * @return Matching recipient type in this enumeration
	 */
	implicit def convertFrom(recipientType: JRecipientType): RecipientType = recipientType match {
		case Message.RecipientType.TO => Primary
		case Message.RecipientType.CC => Copy
		case Message.RecipientType.BCC => HiddenCopy
	}
	
	/**
	  * @param id id representing a recipient type
	  * @return recipient type matching the specified id. None if the id didn't match any recipient type
	  */
	def findForId(id: Int) = values.find { _.id == id }
	
	/**
	  * @param id id matching a recipient type
	  * @return recipient type matching that id, or the default recipient type (primary)
	  */
	def forId(id: Int) = findForId(id).getOrElse(default)
	
	/**
	  * @param value A value representing an recipient type id
	  * @return recipient type matching the specified value, 
		when the value is interpreted as an recipient type id, 
	  * or the default recipient type (primary)
	  */
	def fromValue(value: Value) = forId(value.getInt)
	
	
	// NESTED	--------------------
	
	/**
	  * Represents an additional (secondary) recipient of a message
	  * @since 15.10.2023
	  */
	case object Copy extends RecipientType
	{
		// ATTRIBUTES	--------------------
		
		override val id = 2
	}
	
	/**
	  * Represents a recipient of a message not visible to other recipients
	  * @since 15.10.2023
	  */
	case object HiddenCopy extends RecipientType
	{
		// ATTRIBUTES	--------------------
		
		override val id = 3
	}
	
	/**
	  * Represents a primary recipient of a message
	  * @since 15.10.2023
	  */
	case object Primary extends RecipientType
	{
		// ATTRIBUTES	--------------------
		
		override val id = 1
	}
}

