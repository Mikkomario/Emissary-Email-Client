package vf.emissary.model.stored.messaging

import utopia.flow.generic.model.template.HasPropertiesLike.HasProperties
import utopia.vault.store.{FromIdFactory, StoredFromModelFactory, StoredModelConvertible}
import vf.emissary.database.access.single.messaging.service.user.DbSingleEmailServiceUser
import vf.emissary.model.factory.messaging.EmailServiceUserFactoryWrapper
import vf.emissary.model.partial.messaging.EmailServiceUserData

object EmailServiceUser extends StoredFromModelFactory[EmailServiceUserData, EmailServiceUser]
{
	// IMPLEMENTED	--------------------
	
	override def dataFactory = EmailServiceUserData
	
	override protected def complete(model: HasProperties, data: EmailServiceUserData) =
		model("id").tryInt.map { apply(_, data) }
}

/**
  * Represents a email service user that has already been stored in the database
  * @param id   id of this email service user in the database
  * @param data Wrapped email service user data
  * @author Mikko Hilpinen
  * @since 22.12.2024, v1.1
  */
case class EmailServiceUser(id: Int, data: EmailServiceUserData) 
	extends StoredModelConvertible[EmailServiceUserData] with FromIdFactory[Int, EmailServiceUser] 
		with EmailServiceUserFactoryWrapper[EmailServiceUserData, EmailServiceUser]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this email service user in the database
	  */
	def access = DbSingleEmailServiceUser(id)
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory = data
	
	override def withId(id: Int) = copy(id = id)
	
	override protected def wrap(data: EmailServiceUserData) = copy(data = data)
}

