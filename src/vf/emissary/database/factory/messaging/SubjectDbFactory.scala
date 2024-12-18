package vf.emissary.database.factory.messaging

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import utopia.vault.sql.OrderBy
import vf.emissary.database.storable.messaging.SubjectDbModel
import vf.emissary.model.partial.messaging.SubjectData
import vf.emissary.model.stored.messaging.Subject

/**
  * Used for reading subject data from the DB
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object SubjectDbFactory extends FromValidatedRowModelFactory[Subject]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	def model = SubjectDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering: Option[OrderBy] = None
	
	override def table = model.table
	
	override protected def fromValidatedModel(valid: Model) = 
		Subject(valid(this.model.id.name).getInt, SubjectData(valid(this.model.created.name).getInstant))
}

