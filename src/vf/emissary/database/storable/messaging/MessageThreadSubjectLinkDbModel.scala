package vf.emissary.database.storable.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.HasIdProperty
import utopia.vault.store.{FromIdFactory, HasId}
import utopia.vault.nosql.storable.StorableFactory
import vf.emissary.database.EmissaryTables
import vf.emissary.model.factory.messaging.MessageThreadSubjectLinkFactory
import vf.emissary.model.partial.messaging.MessageThreadSubjectLinkData
import vf.emissary.model.stored.messaging.MessageThreadSubjectLink

import java.time.Instant

/**
  * Used
  * 
	 for constructing MessageThreadSubjectLinkDbModel instances and for inserting message thread subject links to the database
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object MessageThreadSubjectLinkDbModel 
	extends StorableFactory[MessageThreadSubjectLinkDbModel, MessageThreadSubjectLink, MessageThreadSubjectLinkData] 
		with FromIdFactory[Int, MessageThreadSubjectLinkDbModel] with HasIdProperty 
		with MessageThreadSubjectLinkFactory[MessageThreadSubjectLinkDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with thread ids
	  */
	lazy val threadId = property("threadId")
	
	/**
	  * Database property used for interacting with subject ids
	  */
	lazy val subjectId = property("subjectId")
	
	/**
	  * Database property used for interacting with creation times
	  */
	lazy val created = property("created")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = EmissaryTables.messageThreadSubjectLink
	
	override def apply(data: MessageThreadSubjectLinkData): MessageThreadSubjectLinkDbModel = 
		apply(None, Some(data.threadId), Some(data.subjectId), Some(data.created))
	
	/**
	  * @param created Time when this subject was first used in the specified thread
	  * @return A model containing only the specified created
	  */
	override def withCreated(created: Instant) = apply(created = Some(created))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	/**
	  * @param subjectId Id of the subject used in the specified thread
	  * @return A model containing only the specified subject id
	  */
	override def withSubjectId(subjectId: Int) = apply(subjectId = Some(subjectId))
	
	/**
	  * @param threadId Id of the thread where the referenced subject was used
	  * @return A model containing only the specified thread id
	  */
	override def withThreadId(threadId: Int) = apply(threadId = Some(threadId))
	
	override protected def complete(id: Value, data: MessageThreadSubjectLinkData) = 
		MessageThreadSubjectLink(id.getInt, data)
}

/**
  * Used for interacting with MessageThreadSubjectLinks in the database
  * @param id message thread subject link database id
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
case class MessageThreadSubjectLinkDbModel(id: Option[Int] = None, threadId: Option[Int] = None, 
	subjectId: Option[Int] = None, created: Option[Instant] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, MessageThreadSubjectLinkDbModel] 
		with MessageThreadSubjectLinkFactory[MessageThreadSubjectLinkDbModel]
{
	// IMPLEMENTED	--------------------
	
	override def table = MessageThreadSubjectLinkDbModel.table
	
	override def valueProperties = 
		Vector(MessageThreadSubjectLinkDbModel.id.name -> id, 
			MessageThreadSubjectLinkDbModel.threadId.name -> threadId, 
			MessageThreadSubjectLinkDbModel.subjectId.name -> subjectId, 
			MessageThreadSubjectLinkDbModel.created.name -> created)
	
	/**
	  * @param created Time when this subject was first used in the specified thread
	  * @return A new copy of this model with the specified created
	  */
	override def withCreated(created: Instant) = copy(created = Some(created))
	
	override def withId(id: Int) = copy(id = Some(id))
	
	/**
	  * @param subjectId Id of the subject used in the specified thread
	  * @return A new copy of this model with the specified subject id
	  */
	override def withSubjectId(subjectId: Int) = copy(subjectId = Some(subjectId))
	
	/**
	  * @param threadId Id of the thread where the referenced subject was used
	  * @return A new copy of this model with the specified thread id
	  */
	override def withThreadId(threadId: Int) = copy(threadId = Some(threadId))
}

