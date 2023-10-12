package vf.emissary.model.partial.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now

import java.time.Instant

object MessageThreadSubjectLinkData extends FromModelFactoryWithSchema[MessageThreadSubjectLinkData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("threadId", IntType, Vector("thread_id")), 
			PropertyDeclaration("subjectId", IntType, Vector("subject_id")), PropertyDeclaration("created", 
			InstantType, isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		MessageThreadSubjectLinkData(valid("threadId").getInt, valid("subjectId").getInt, 
			valid("created").getInstant)
}

/**
  * Connects a subject with a message thread in which it was used
  * @param threadId Id of the thread where the referenced subject was used
  * @param subjectId Id of the subject used in the specified thread
  * @param created Time when this subject was first used in the specified thread
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class MessageThreadSubjectLinkData(threadId: Int, subjectId: Int, created: Instant = Now) 
	extends ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("threadId" -> threadId, "subjectId" -> subjectId, 
		"created" -> created))
}

