package vf.emissary.database.model.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.emissary.database.factory.messaging.MessageThreadSubjectLinkFactory
import vf.emissary.model.partial.messaging.MessageThreadSubjectLinkData
import vf.emissary.model.stored.messaging.MessageThreadSubjectLink

import java.time.Instant

/**
  * Used for constructing MessageThreadSubjectLinkModel instances and for inserting message thread subject
  *  links to the database
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object MessageThreadSubjectLinkModel 
	extends DataInserter[MessageThreadSubjectLinkModel, MessageThreadSubjectLink, 
		MessageThreadSubjectLinkData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains message thread subject link thread id
	  */
	val threadIdAttName = "threadId"
	
	/**
	  * Name of the property that contains message thread subject link subject id
	  */
	val subjectIdAttName = "subjectId"
	
	/**
	  * Name of the property that contains message thread subject link created
	  */
	val createdAttName = "created"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains message thread subject link thread id
	  */
	def threadIdColumn = table(threadIdAttName)
	
	/**
	  * Column that contains message thread subject link subject id
	  */
	def subjectIdColumn = table(subjectIdAttName)
	
	/**
	  * Column that contains message thread subject link created
	  */
	def createdColumn = table(createdAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = MessageThreadSubjectLinkFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: MessageThreadSubjectLinkData) = 
		apply(None, Some(data.threadId), Some(data.subjectId), Some(data.created))
	
	override protected def complete(id: Value, data: MessageThreadSubjectLinkData) = 
		MessageThreadSubjectLink(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param created Time when this subject was first used in the specified thread
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param id A message thread subject link id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param subjectId Id of the subject used in the specified thread
	  * @return A model containing only the specified subject id
	  */
	def withSubjectId(subjectId: Int) = apply(subjectId = Some(subjectId))
	
	/**
	  * @param threadId Id of the thread where the referenced subject was used
	  * @return A model containing only the specified thread id
	  */
	def withThreadId(threadId: Int) = apply(threadId = Some(threadId))
}

/**
  * Used for interacting with MessageThreadSubjectLinks in the database
  * @param id message thread subject link database id
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class MessageThreadSubjectLinkModel(id: Option[Int] = None, threadId: Option[Int] = None, 
	subjectId: Option[Int] = None, created: Option[Instant] = None) 
	extends StorableWithFactory[MessageThreadSubjectLink]
{
	// IMPLEMENTED	--------------------
	
	override def factory = MessageThreadSubjectLinkModel.factory
	
	override def valueProperties = {
		import MessageThreadSubjectLinkModel._
		Vector("id" -> id, threadIdAttName -> threadId, subjectIdAttName -> subjectId, 
			createdAttName -> created)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param created Time when this subject was first used in the specified thread
	  * @return A new copy of this model with the specified created
	  */
	def withCreated(created: Instant) = copy(created = Some(created))
	
	/**
	  * @param subjectId Id of the subject used in the specified thread
	  * @return A new copy of this model with the specified subject id
	  */
	def withSubjectId(subjectId: Int) = copy(subjectId = Some(subjectId))
	
	/**
	  * @param threadId Id of the thread where the referenced subject was used
	  * @return A new copy of this model with the specified thread id
	  */
	def withThreadId(threadId: Int) = copy(threadId = Some(threadId))
}

