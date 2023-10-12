package vf.emissary.database.model.text

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.emissary.database.factory.text.DelimiterFactory
import vf.emissary.model.partial.text.DelimiterData
import vf.emissary.model.stored.text.Delimiter

import java.time.Instant

/**
  * Used for constructing DelimiterModel instances and for inserting delimiters to the database
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DelimiterModel extends DataInserter[DelimiterModel, Delimiter, DelimiterData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains delimiter text
	  */
	val textAttName = "text"
	
	/**
	  * Name of the property that contains delimiter created
	  */
	val createdAttName = "created"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains delimiter text
	  */
	def textColumn = table(textAttName)
	
	/**
	  * Column that contains delimiter created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = DelimiterFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: DelimiterData) = apply(None, data.text, Some(data.created))
	
	override protected def complete(id: Value, data: DelimiterData) = Delimiter(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param created Time when this delimiter was added to the database
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param id A delimiter id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param text The characters that form this delimiter
	  * @return A model containing only the specified text
	  */
	def withText(text: String) = apply(text = text)
}

/**
  * Used for interacting with Delimiters in the database
  * @param id delimiter database id
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class DelimiterModel(id: Option[Int] = None, text: String = "", created: Option[Instant] = None) 
	extends StorableWithFactory[Delimiter]
{
	// IMPLEMENTED	--------------------
	
	override def factory = DelimiterModel.factory
	
	override def valueProperties = {
		import DelimiterModel._
		Vector("id" -> id, textAttName -> text, createdAttName -> created)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param created Time when this delimiter was added to the database
	  * @return A new copy of this model with the specified created
	  */
	def withCreated(created: Instant) = copy(created = Some(created))
	
	/**
	  * @param text The characters that form this delimiter
	  * @return A new copy of this model with the specified text
	  */
	def withText(text: String) = copy(text = text)
}

