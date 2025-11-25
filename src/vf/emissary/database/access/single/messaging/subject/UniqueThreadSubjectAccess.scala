package vf.emissary.database.access.single.messaging.subject

import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.ThreadSubjectDbFactory
import vf.emissary.database.storable.messaging.MessageThreadSubjectLinkDbModel
import vf.emissary.model.combined.messaging.ThreadSubject

object UniqueThreadSubjectAccess extends ViewFactory[UniqueThreadSubjectAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): UniqueThreadSubjectAccess = 
		_UniqueThreadSubjectAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _UniqueThreadSubjectAccess(override val accessCondition: Option[Condition]) 
		extends UniqueThreadSubjectAccess
}

/**
  * A common trait for access points that return distinct thread subjects
  * @author Mikko Hilpinen
  * @since 17.10.2023, v0.1
  */
trait UniqueThreadSubjectAccess 
	extends UniqueSubjectAccessLike[ThreadSubject, UniqueThreadSubjectAccess] with SingleRowModelAccess[ThreadSubject]
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the thread where the referenced subject was used. 
	  * None if no message thread subject link (or value) was found.
	  */
	def threadLinkThreadId(implicit connection: Connection) = pullColumn(threadLinkModel.threadId.column).int
	/**
	  * Id of the subject used in the specified thread. 
	  * None if no message thread subject link (or value) was found.
	  */
	def threadLinkSubjectId(implicit connection: Connection) =
		pullColumn(threadLinkModel.subjectId.column).int
	/**
	  * Time when this subject was first used in the specified thread. 
	  * None if no message thread subject link (or value) was found.
	  */
	def threadLinkCreated(implicit connection: Connection) = pullColumn(threadLinkModel
		.created.column).instant
	
	/**
	  * A database model (factory) used for interacting with the linked thread link
	  */
	protected def threadLinkModel = MessageThreadSubjectLinkDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = ThreadSubjectDbFactory
	override def self = this
	
	override def apply(condition: Condition): UniqueThreadSubjectAccess = UniqueThreadSubjectAccess(condition)
}

