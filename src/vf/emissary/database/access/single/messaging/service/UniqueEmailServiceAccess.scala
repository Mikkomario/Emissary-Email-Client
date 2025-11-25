package vf.emissary.database.access.single.messaging.service

import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{FilterableView, ViewFactory}
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.EmailServiceDbFactory
import vf.emissary.database.storable.messaging.EmailServiceDbModel
import vf.emissary.model.stored.messaging.EmailService

object UniqueEmailServiceAccess extends ViewFactory[UniqueEmailServiceAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): UniqueEmailServiceAccess = 
		_UniqueEmailServiceAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _UniqueEmailServiceAccess(override val accessCondition: Option[Condition]) 
		extends UniqueEmailServiceAccess
}

/**
  * A common trait for access points that return individual and distinct email services.
  * @author Mikko Hilpinen
  * @since 22.12.2024, v1.1
  */
trait UniqueEmailServiceAccess 
	extends SingleRowModelAccess[EmailService] 
		with DistinctModelAccess[EmailService, Option[EmailService], Value] 
		with FilterableView[UniqueEmailServiceAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Connection address of this (IMAP/SMTP) service. 
	  * None if no email service (or value) was found.
	  */
	def address(implicit connection: Connection) = pullColumn(model.address.column).getString
	
	/**
	  * Time when this email service was added to the database. 
	  * None if no email service (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.created.column).instant
	
	/**
	  * Name of this email service. Empty if not defined. 
	  * None if no email service (or value) was found.
	  */
	def name(implicit connection: Connection) = pullColumn(model.name.column).getString
	
	/**
	  * Unique id of the accessible email service. None if no email service was accessible.
	  */
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = EmailServiceDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = EmailServiceDbFactory
	
	override def self = this
	
	override def apply(condition: Condition): UniqueEmailServiceAccess = UniqueEmailServiceAccess(condition)
}

