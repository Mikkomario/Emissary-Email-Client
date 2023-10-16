package vf.emissary.database.access.many.url.request_path

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.sql.Condition
import vf.emissary.database.factory.url.DetailedRequestPathFactory
import vf.emissary.database.model.url.DomainModel
import vf.emissary.model.combined.url.DetailedRequestPath

import java.time.Instant

object ManyDetailedRequestPathsAccess
{
	// NESTED	--------------------
	
	private class SubAccess(condition: Condition) extends ManyDetailedRequestPathsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return multiple detailed request paths at a time
  * @author Mikko Hilpinen
  * @since 16.10.2023
  */
trait ManyDetailedRequestPathsAccess 
	extends ManyRequestPathsAccessLike[DetailedRequestPath, ManyDetailedRequestPathsAccess] 
		with ManyRowModelAccess[DetailedRequestPath]
{
	// COMPUTED	--------------------
	
	/**
	  * urls of the accessible domains
	  */
	def domainUrls(implicit connection: Connection) = pullColumn(domainModel.urlColumn).flatMap { _.string }
	
	/**
	  * creation times of the accessible domains
	  */
	def domainCreationTimes(implicit connection: Connection) = 
		pullColumn(domainModel.createdColumn).map { v => v.getInstant }
	
	/**
	  * Model (factory) used for interacting the domains associated with this detailed request path
	  */
	protected def domainModel = DomainModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = DetailedRequestPathFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyDetailedRequestPathsAccess = 
		new ManyDetailedRequestPathsAccess.SubAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the creation times of the targeted domains
	  * @param newCreated A new created to assign
	  * @return Whether any domain was affected
	  */
	def domainCreationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(domainModel.createdColumn, newCreated)
	
	/**
	  * Updates the urls of the targeted domains
	  * @param newUrl A new url to assign
	  * @return Whether any domain was affected
	  */
	def domainUrls_=(newUrl: String)(implicit connection: Connection) = putColumn(domainModel.urlColumn, 
		newUrl)
}

