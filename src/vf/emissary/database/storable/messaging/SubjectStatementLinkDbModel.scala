package vf.emissary.database.storable.messaging

import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.DbPropertyDeclaration
import vf.emissary.database.EmissaryTables
import utopia.logos.database.props.text.StatementPlacementDbProps
import utopia.logos.database.storable.text.{StatementPlacementDbModel, StatementPlacementDbModelFactoryLike, StatementPlacementDbModelLike}
import vf.emissary.model.factory.messaging.SubjectStatementLinkFactory
import vf.emissary.model.partial.messaging.SubjectStatementLinkData
import vf.emissary.model.stored.messaging.SubjectStatementLink

/**
  * Used for constructing SubjectStatementLinkDbModel instances and for inserting subject statement links to the database
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object SubjectStatementLinkDbModel 
	extends StatementPlacementDbModelFactoryLike[SubjectStatementLinkDbModel, SubjectStatementLink, SubjectStatementLinkData] 
		with SubjectStatementLinkFactory[SubjectStatementLinkDbModel] with StatementPlacementDbProps
{
	// ATTRIBUTES	--------------------
	
	override lazy val id = DbPropertyDeclaration("id", index)
	/**
	  * Database property used for interacting with subject ids
	  */
	lazy val subjectId = property("subjectId")
	/**
	  * Database property used for interacting with statement ids
	  */
	override lazy val statementId = property("statementId")
	/**
	  * Database property used for interacting with order indices
	  */
	override lazy val orderIndex = property("orderIndex")
	
	
	// IMPLEMENTED	--------------------
	
	override def parentId = subjectId
	
	override def table = EmissaryTables.subjectStatementLink
	
	override def apply(data: SubjectStatementLinkData): SubjectStatementLinkDbModel = 
		apply(None, Some(data.subjectId), Some(data.statementId), Some(data.orderIndex))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	/**
	  * @param orderIndex 0-based index that indicates the specific location of the placed text
	  * @return A model containing only the specified order index
	  */
	override def withOrderIndex(orderIndex: Int) = apply(orderIndex = Some(orderIndex))
	
	/**
	  * @param statementId Id of the placed statement
	  * @return A model containing only the specified statement id
	  */
	override def withStatementId(statementId: Int) = apply(statementId = Some(statementId))
	
	/**
	  * @param subjectId Id of the described subject
	  * @return A model containing only the specified subject id
	  */
	override def withSubjectId(subjectId: Int) = apply(subjectId = Some(subjectId))
	
	override protected def complete(id: Value, data: SubjectStatementLinkData) = 
		SubjectStatementLink(id.getInt, data)
}

/**
  * Used for interacting with SubjectStatementLinks in the database
  * @param id subject statement link database id
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
case class SubjectStatementLinkDbModel(id: Option[Int] = None, subjectId: Option[Int] = None,
                                       statementId: Option[Int] = None, orderIndex: Option[Int] = None)
	extends StatementPlacementDbModel with StatementPlacementDbModelLike[SubjectStatementLinkDbModel] 
		with SubjectStatementLinkFactory[SubjectStatementLinkDbModel]
{
	// IMPLEMENTED	--------------------
	
	override def dbProps = SubjectStatementLinkDbModel
	
	override def parentId = subjectId
	
	override def table = SubjectStatementLinkDbModel.table
	
	/**
	  * @param id Id to assign to the new model (default = currently assigned id)
	  * @param parentId parent id to assign to the new model (default = currently assigned value)
	  * @param statementId statement id to assign to the new model (default = currently assigned value)
	  * @param orderIndex order index to assign to the new model (default = currently assigned value)
	  */
	override def copyStatementPlacement(id: Option[Int] = id, parentId: Option[Int] = parentId, 
		statementId: Option[Int] = statementId, orderIndex: Option[Int] = orderIndex) = 
		copy(id = id, subjectId = parentId, statementId = statementId, orderIndex = orderIndex)
	
	/**
	  * @param subjectId Id of the described subject
	  * @return A new copy of this model with the specified subject id
	  */
	override def withSubjectId(subjectId: Int) = copy(subjectId = Some(subjectId))
}

