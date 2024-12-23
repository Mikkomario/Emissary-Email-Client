package vf.emissary.model.partial.messaging

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.{IntType, LongType, StringType}
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.parse.file.FileExtensions._
import utopia.flow.util.EitherExtensions._
import vf.emissary.model.factory.messaging.AttachmentFactory
import vf.emissary.util.Common._

import java.nio.file.Path

object AttachmentData extends FromModelFactoryWithSchema[AttachmentData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = ModelDeclaration(Vector(
		PropertyDeclaration("messageId", IntType, Single("message_id")),
		PropertyDeclaration("relativePath", StringType, Single("relative_path")),
		PropertyDeclaration("size", LongType)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) =
		AttachmentData(valid("messageId").getInt, valid("relativePath").getString: Path, valid("size").getLong)
}

/**
  * Represents an attached file within a message
  * @param messageId Id of the message to which this file is attached
  * @param relativePath Name of the attached file, as appears on the file system
  * @param size Size of this attachment in bytes
  * @author Mikko Hilpinen
  * @since 13.10.2023, v0.1
  */
case class AttachmentData(messageId: Int, relativePath: Path, size: Long) 
	extends AttachmentFactory[AttachmentData] with ModelConvertible
{
	// ATTRIBUTES   --------------------
	
	/**
	 * Path to this attachment file
	 */
	lazy val path = relativePath.relativeTo(attachmentsDirectory).either
	
	
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("messageId" -> messageId, "relativePath" -> relativePath.toJson, "size" -> size))
	
	override def withMessageId(messageId: Int) = copy(messageId = messageId)
	override def withRelativePath(relativePath: Path) = copy(relativePath = relativePath)
	override def withSize(size: Long) = copy(size = size)
}

