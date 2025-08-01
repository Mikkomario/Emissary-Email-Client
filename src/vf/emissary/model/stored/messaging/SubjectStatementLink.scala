package vf.emissary.model.stored.messaging

import utopia.flow.generic.model.template.ModelLike.AnyModel
import utopia.logos.model.partial.text.StatementPlacementData
import utopia.logos.model.stored.text.StoredStatementPlacementLike
import utopia.vault.store.StoredFromModelFactory
import vf.emissary.database.access.single.messaging.subject.link.statement.DbSingleSubjectStatementLink
import vf.emissary.model.factory.messaging.SubjectStatementLinkFactoryWrapper
import vf.emissary.model.partial.messaging.SubjectStatementLinkData

object SubjectStatementLink extends StoredFromModelFactory[SubjectStatementLinkData, SubjectStatementLink]
{
	// IMPLEMENTED	--------------------
	
	override def dataFactory = SubjectStatementLinkData
	
	override protected def complete(model: AnyModel, data: SubjectStatementLinkData) = 
		model("id").tryInt.map { apply(_, data) }
}

/**
  * Represents a subject statement link that has already been stored in the database
  * @param id id of this subject statement link in the database
  * @param data Wrapped subject statement link data
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class SubjectStatementLink(id: Int, data: SubjectStatementLinkData) 
	extends SubjectStatementLinkFactoryWrapper[SubjectStatementLinkData, SubjectStatementLink]
		with StatementPlacementData with StoredStatementPlacementLike[SubjectStatementLinkData, SubjectStatementLink]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this subject statement link in the database
	  */
	def access = DbSingleSubjectStatementLink(id)
	
	
	// IMPLEMENTED	--------------------
	
	override def withId(id: Int) = copy(id = id)
	
	override protected def wrap(data: SubjectStatementLinkData) = copy(data = data)
}

