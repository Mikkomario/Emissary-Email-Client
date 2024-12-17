package vf.emissary.database.access.many.url.domain

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{FilterableView, ViewFactory}
import utopia.vault.sql.Condition
import vf.emissary.database.factory.url.DomainFactory
import vf.emissary.database.model.url.DomainModel
import vf.emissary.model.stored.url.Domain

import java.time.Instant

object ManyDomainsAccess extends ViewFactory[ManyDomainsAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyDomainsAccess = _ManyDomainsAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyDomainsAccess(override val accessCondition: Option[Condition]) 
		extends ManyDomainsAccess
}

/**
  * A common trait for access points which target multiple domains at a time
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
trait ManyDomainsAccess extends ManyRowModelAccess[Domain] with FilterableView[ManyDomainsAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * urls of the accessible domains
	  */
	def urls(implicit connection: Connection) = pullColumn(model.urlColumn).flatMap { _.string }
	
	/**
	  * creation times of the accessible domains
	  */
	def creationTimes(implicit connection: Connection) = {
		pullColumn(model.createdColumn).map 
		{
			 v => v.getInstant 
		}
	}
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * A map containing all accessible domains as url-id pairs.
	  * All urls are in lower case.
	  * @param connection Implicit DB connection
	  */
	def toMap(implicit connection: Connection) = {
			pullColumnMap(model.urlColumn, index).map 
			{
				 case (urlVal, 
						idVal) => urlVal.getString.toLowerCase -> idVal.getInt 
			}
	}
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = DomainModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = DomainFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): ManyDomainsAccess = ManyDomainsAccess(condition)
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the creation times of the targeted domains
	  * @param newCreated A new created to assign
	  * @return Whether any domain was affected
	  */
	def creationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.createdColumn, newCreated)
	
	/**
	  * @param domainUrls Targeted domain URLs
	  * @return Access to domains using those specific urls
	  */
	def matching(domainUrls: Iterable[String]) = filter(model.urlColumn.in(domainUrls))
	
	/**
	  * Updates the urls of the targeted domains
	  * @param newUrl A new url to assign
	  * @return Whether any domain was affected
	  */
	def urls_=(newUrl: String)(implicit connection: Connection) = putColumn(model.urlColumn, newUrl)
}

