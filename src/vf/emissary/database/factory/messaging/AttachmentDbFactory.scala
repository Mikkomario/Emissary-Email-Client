package vf.emissary.database.factory.messaging

import utopia.flow.generic.model.immutable.Model
import utopia.flow.parse.file.FileExtensions._
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import utopia.vault.sql.OrderBy
import vf.emissary.database.storable.messaging.AttachmentDbModel
import vf.emissary.model.partial.messaging.AttachmentData
import vf.emissary.model.stored.messaging.Attachment

import java.nio.file.Path

/**
  * Used for reading attachment data from the DB
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object AttachmentDbFactory extends FromValidatedRowModelFactory[Attachment]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	val model = AttachmentDbModel
	
	override lazy val defaultOrdering: Option[OrderBy] = None
	
	
	// IMPLEMENTED	--------------------
	
	override def table = model.table
	
	override protected def fromValidatedModel(valid: Model) = 
		Attachment(valid(this.model.id.name).getInt, 
			AttachmentData(valid(this.model.relativePath.name).getString: Path, 
			valid(this.model.size.name).getLong))
}

