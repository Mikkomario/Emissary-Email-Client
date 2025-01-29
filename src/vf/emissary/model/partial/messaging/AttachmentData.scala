package vf.emissary.model.partial.messaging

import utopia.flow.collection.immutable.{Pair, Single}
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.{LongType, StringType}
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.parse.file.FileExtensions._
import vf.emissary.database.EmissaryContext
import vf.emissary.model.factory.messaging.AttachmentFactory

import java.nio.file.Path

object AttachmentData extends FromModelFactoryWithSchema[AttachmentData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Pair(PropertyDeclaration("relativePath", StringType, Single("relative_path")),
			PropertyDeclaration("size", LongType)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		AttachmentData(valid("relativePath").getString: Path, valid("size").getLong)
}

/**
  * Represents an attached file within a message
  * @param relativePath Name of the attached file, as appears on the file system
  * @param size         Size of this attachment in bytes
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
case class AttachmentData(relativePath: Path, size: Long) 
	extends AttachmentFactory[AttachmentData] with ModelConvertible
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Path to this attachment file
	  */
	lazy val path = EmissaryContext.attachmentsDirectory match {
		case Some(dir) => dir/relativePath
		case None => throw new IllegalStateException("Attachments are not supported")
	}
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Pair("relativePath" -> relativePath.toJson, "size" -> size))
	
	override def withRelativePath(relativePath: Path) = copy(relativePath = relativePath)
	override def withSize(size: Long) = copy(size = size)
}

