package vf.emissary.database.model.url

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.emissary.database.factory.url.DomainFactory
import vf.emissary.model.partial.url.DomainData
import vf.emissary.model.stored.url.Domain

import java.time.Instant

/**
  * Used for constructing DomainModel instances and for inserting domains to the database
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
object DomainModel extends DataInserter[DomainModel, Domain, DomainData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains domain url
	  */
	val urlAttName = "url"
	
	/**
	  * Name of the property that contains domain created
	  */
	val createdAttName = "created"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains domain url
	  */
	def urlColumn = table(urlAttName)
	
	/**
	  * Column that contains domain created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = DomainFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: DomainData) = apply(None, data.url, Some(data.created))
	
	override protected def complete(id: Value, data: DomainData) = Domain(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param created Time when this domain was added to the database
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param id A domain id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param url Full http(s) address of this domain in string format. Includes protocol, 
	  * domain name and possible port number.
	  * @return A model containing only the specified url
	  */
	def withUrl(url: String) = apply(url = url)
}

/**
  * Used for interacting with Domains in the database
  * @param id domain database id
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
case class DomainModel(id: Option[Int] = None, url: String = "", created: Option[Instant] = None) 
	extends StorableWithFactory[Domain]
{
	// IMPLEMENTED	--------------------
	
	override def factory = DomainModel.factory
	
	override def valueProperties = {
		import DomainModel._
		Vector("id" -> id, urlAttName -> url, createdAttName -> created)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param created Time when this domain was added to the database
	  * @return A new copy of this model with the specified created
	  */
	def withCreated(created: Instant) = copy(created = Some(created))
	
	/**
	  * @param url Full http(s) address of this domain in string format. Includes protocol, 
	  * domain name and possible port number.
	  * @return A new copy of this model with the specified url
	  */
	def withUrl(url: String) = copy(url = url)
}

