package vf.emissary.database.access.many.messaging.service

import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{FilterableView, ViewFactory}
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.EmailServiceDbFactory
import vf.emissary.database.storable.messaging.EmailServiceDbModel
import vf.emissary.model.stored.messaging.EmailService

object ManyEmailServicesAccess extends ViewFactory[ManyEmailServicesAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override
		 def apply(condition: Condition): ManyEmailServicesAccess = _ManyEmailServicesAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyEmailServicesAccess(override val accessCondition: Option[Condition]) 
		extends ManyEmailServicesAccess
}

/**
  * A common trait for access points which target multiple email services at a time
  * @author Mikko Hilpinen
  * @since 22.12.2024, v1.1
  */
trait ManyEmailServicesAccess 
	extends ManyRowModelAccess[EmailService] with FilterableView[ManyEmailServicesAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * addressses of the accessible email services
	  */
	def addressses(implicit connection: Connection) = pullColumn(model.address.column).flatMap { _.string }
	
	/**
	  * creation times of the accessible email services
	  */
	def creationTimes(implicit connection: Connection) = 
		pullColumn(model.created.column).map { v => v.getInstant }
	
	/**
	  * names of the accessible email services
	  */
	def names(implicit connection: Connection) = pullColumn(model.name.column).flatMap { _.string }
	
	/**
	  * Unique ids of the accessible email services
	  */
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = EmailServiceDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = EmailServiceDbFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): ManyEmailServicesAccess = ManyEmailServicesAccess(condition)
}

