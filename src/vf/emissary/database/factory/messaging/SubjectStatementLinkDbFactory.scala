package vf.emissary.database.factory.messaging

import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import utopia.vault.sql.OrderBy
import utopia.logos.database.factory.text.StatementPlacementDbFactoryLike
import vf.emissary.database.storable.messaging.SubjectStatementLinkDbModel
import vf.emissary.model.partial.messaging.SubjectStatementLinkData
import vf.emissary.model.stored.messaging.SubjectStatementLink

/**
  * Used for reading subject statement link data from the DB
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object SubjectStatementLinkDbFactory extends StatementPlacementDbFactoryLike[SubjectStatementLink]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	override def dbProps = SubjectStatementLinkDbModel
	
	override def defaultOrdering: Option[OrderBy] = None
	
	override def table = dbProps.table
	
	/**
	  * @param model Model from which additional data may be read
	  * @param id Id to assign to the read/parsed statement placement
	  * @param parentId parent id to assign to the new statement placement
	  * @param statementId statement id to assign to the new statement placement
	  * @param orderIndex order index to assign to the new statement placement
	  */
	override protected def apply(model: HasProperties, id: Int, parentId: Int, statementId: Int, orderIndex: Int) =
		SubjectStatementLink(id, SubjectStatementLinkData(parentId, statementId, orderIndex))
}

