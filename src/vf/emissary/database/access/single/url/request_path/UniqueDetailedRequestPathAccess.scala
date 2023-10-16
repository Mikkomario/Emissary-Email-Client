package vf.emissary.database.access.single.url.request_path

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.emissary.database.factory.url.DetailedRequestPathFactory
import vf.emissary.database.model.url.DomainModel
import vf.emissary.model.combined.url.DetailedRequestPath

import java.time.Instant

object UniqueDetailedRequestPathAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueDetailedRequestPathAccess = 
		new _UniqueDetailedRequestPathAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueDetailedRequestPathAccess(condition: Condition)
		 extends UniqueDetailedRequestPathAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return distinct detailed request paths
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
trait UniqueDetailedRequestPathAccess 
	extends UniqueRequestPathAccessLike[DetailedRequestPath] with SingleRowModelAccess[DetailedRequestPath] 
		with FilterableView[UniqueDetailedRequestPathAccess]
{
	// COMPUTED	--------------------
	
	/**
	  * Full http(s) address of this domain in string format. Includes protocol, 
	  * domain name and possible port number.. None if no domain (or value) was found.
	  */
	def domainUrl(implicit connection: Connection) = pullColumn(domainModel.urlColumn).getString
	
	/**
	  * Time when this domain was added to the database. None if no domain (or value) was found.
	  */
	def domainCreated(implicit connection: Connection) = pullColumn(domainModel.createdColumn).instant
	
	/**
	  * A database model (factory) used for interacting with the linked domain
	  */
	protected def domainModel = DomainModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = DetailedRequestPathFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueDetailedRequestPathAccess = 
		new UniqueDetailedRequestPathAccess._UniqueDetailedRequestPathAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the creation times of the targeted domains
	  * @param newCreated A new created to assign
	  * @return Whether any domain was affected
	  */
	def domainCreated_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(domainModel.createdColumn, newCreated)
	
	/**
	  * Updates the urls of the targeted domains
	  * @param newUrl A new url to assign
	  * @return Whether any domain was affected
	  */
	def domainUrl_=(newUrl: String)(implicit connection: Connection) = putColumn(domainModel.urlColumn, 
		newUrl)
}

