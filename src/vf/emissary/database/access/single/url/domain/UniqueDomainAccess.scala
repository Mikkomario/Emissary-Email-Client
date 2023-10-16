package vf.emissary.database.access.single.url.domain

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.url.DomainFactory
import vf.emissary.database.model.url.DomainModel
import vf.emissary.model.stored.url.Domain

import java.time.Instant

object UniqueDomainAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueDomainAccess = new _UniqueDomainAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueDomainAccess(condition: Condition) extends UniqueDomainAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct domains.
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
trait UniqueDomainAccess 
	extends SingleRowModelAccess[Domain] with FilterableView[UniqueDomainAccess] 
		with DistinctModelAccess[Domain, Option[Domain], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Full http(s) address of this domain in string format. Includes protocol, 
	  * domain name and possible port number.. None if no domain (or value) was found.
	  */
	def url(implicit connection: Connection) = pullColumn(model.urlColumn).getString
	
	/**
	  * Time when this domain was added to the database. None if no domain (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.createdColumn).instant
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = DomainModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = DomainFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueDomainAccess = 
		new UniqueDomainAccess._UniqueDomainAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the creation times of the targeted domains
	  * @param newCreated A new created to assign
	  * @return Whether any domain was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * Updates the urls of the targeted domains
	  * @param newUrl A new url to assign
	  * @return Whether any domain was affected
	  */
	def url_=(newUrl: String)(implicit connection: Connection) = putColumn(model.urlColumn, newUrl)
}

