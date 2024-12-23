package vf.emissary.model.stored.messaging

import utopia.flow.generic.model.template.ModelLike.AnyModel
import utopia.vault.model.template.{FromIdFactory, StoredFromModelFactory, StoredModelConvertible}
import vf.emissary.database.access.single.messaging.service.DbSingleEmailService
import vf.emissary.model.factory.messaging.EmailServiceFactoryWrapper
import vf.emissary.model.partial.messaging.EmailServiceData

object EmailService extends StoredFromModelFactory[EmailServiceData, EmailService]
{
	// IMPLEMENTED	--------------------
	
	override def dataFactory = EmailServiceData
	
	override protected def complete(model: AnyModel, data: EmailServiceData) = 
		model("id").tryInt.map { apply(_, data) }
}

/**
  * Represents a email service that has already been stored in the database
  * @param id id of this email service in the database
  * @param data Wrapped email service data
  * @author Mikko Hilpinen
  * @since 22.12.2024, v1.1
  */
case class EmailService(id: Int, data: EmailServiceData) 
	extends StoredModelConvertible[EmailServiceData] with FromIdFactory[Int, EmailService] 
		with EmailServiceFactoryWrapper[EmailServiceData, EmailService]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this email service in the database
	  */
	def access = DbSingleEmailService(id)
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory = data
	
	override def withId(id: Int) = copy(id = id)
	
	override protected def wrap(data: EmailServiceData) = copy(data = data)
}

