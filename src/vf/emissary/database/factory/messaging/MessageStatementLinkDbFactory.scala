package vf.emissary.database.factory.messaging

import utopia.flow.generic.model.template.ModelLike.AnyModel
import utopia.vault.sql.OrderBy
import vf.emissary.database.factory.text.StatementPlacementDbFactoryLike
import vf.emissary.database.storable.messaging.MessageStatementLinkDbModel
import vf.emissary.model.partial.messaging.MessageStatementLinkData
import vf.emissary.model.stored.messaging.MessageStatementLink

/**
  * Used for reading message statement link data from the DB
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object MessageStatementLinkDbFactory extends StatementPlacementDbFactoryLike[MessageStatementLink]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	override def dbProps = MessageStatementLinkDbModel
	
	override def defaultOrdering: Option[OrderBy] = None
	
	override def table = dbProps.table
	
	/**
	  * @param model Model from which additional data may be read
	  * @param id Id to assign to the read/parsed statement placement
	  * @param parentId parent id to assign to the new statement placement
	  * @param statementId statement id to assign to the new statement placement
	  * @param orderIndex order index to assign to the new statement placement
	  */
	override protected def apply(model: AnyModel, id: Int, parentId: Int, statementId: Int, orderIndex: Int) =
		MessageStatementLink(id, MessageStatementLinkData(parentId, statementId, orderIndex))
}

