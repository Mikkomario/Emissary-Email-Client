package vf.emissary.database.model.messaging

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.storable.DataInserter
import vf.emissary.database.factory.messaging.SubjectStatementLinkFactory
import vf.emissary.model.partial.messaging.SubjectStatementLinkData
import vf.emissary.model.stored.messaging.SubjectStatementLink

/**
  * Used for constructing SubjectStatementLinkModel instances and for inserting subject statement links
  *  to the database
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object SubjectStatementLinkModel 
	extends DataInserter[SubjectStatementLinkModel, SubjectStatementLink, SubjectStatementLinkData]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Name of the property that contains subject statement link subject id
	  */
	val subjectIdAttName = "subjectId"
	
	/**
	  * Name of the property that contains subject statement link statement id
	  */
	val statementIdAttName = "statementId"
	
	/**
	  * Name of the property that contains subject statement link order index
	  */
	val orderIndexAttName = "orderIndex"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Column that contains subject statement link subject id
	  */
	def subjectIdColumn = table(subjectIdAttName)
	
	/**
	  * Column that contains subject statement link statement id
	  */
	def statementIdColumn = table(statementIdAttName)
	
	/**
	  * Column that contains subject statement link order index
	  */
	def orderIndexColumn = table(orderIndexAttName)
	
	/**
	  * The factory object used by this model type
	  */
	def factory = SubjectStatementLinkFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: SubjectStatementLinkData) = 
		apply(None, Some(data.subjectId), Some(data.statementId), Some(data.orderIndex))
	
	override protected def complete(id: Value, data: SubjectStatementLinkData) = 
		SubjectStatementLink(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param id A subject statement link id
	  * @return A model with that id
	  */
	def withId(id: Int) = apply(Some(id))
	
	/**
	  * @param orderIndex Index where this statement appears within the referenced subject (0-based)
	  * @return A model containing only the specified order index
	  */
	def withOrderIndex(orderIndex: Int) = apply(orderIndex = Some(orderIndex))
	
	/**
	  * @param statementId Id of the statement made within the referenced subject
	  * @return A model containing only the specified statement id
	  */
	def withStatementId(statementId: Int) = apply(statementId = Some(statementId))
	
	/**
	  * @param subjectId Id of the described subject
	  * @return A model containing only the specified subject id
	  */
	def withSubjectId(subjectId: Int) = apply(subjectId = Some(subjectId))
}

/**
  * Used for interacting with SubjectStatementLinks in the database
  * @param id subject statement link database id
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class SubjectStatementLinkModel(id: Option[Int] = None, subjectId: Option[Int] = None, 
	statementId: Option[Int] = None, orderIndex: Option[Int] = None) 
	extends StorableWithFactory[SubjectStatementLink]
{
	// IMPLEMENTED	--------------------
	
	override def factory = SubjectStatementLinkModel.factory
	
	override def valueProperties = {
		import SubjectStatementLinkModel._
		Vector("id" -> id, subjectIdAttName -> subjectId, statementIdAttName -> statementId, 
			orderIndexAttName -> orderIndex)
	}
	
	
	// OTHER	--------------------
	
	/**
	  * @param orderIndex Index where this statement appears within the referenced subject (0-based)
	  * @return A new copy of this model with the specified order index
	  */
	def withOrderIndex(orderIndex: Int) = copy(orderIndex = Some(orderIndex))
	
	/**
	  * @param statementId Id of the statement made within the referenced subject
	  * @return A new copy of this model with the specified statement id
	  */
	def withStatementId(statementId: Int) = copy(statementId = Some(statementId))
	
	/**
	  * @param subjectId Id of the described subject
	  * @return A new copy of this model with the specified subject id
	  */
	def withSubjectId(subjectId: Int) = copy(subjectId = Some(subjectId))
}

