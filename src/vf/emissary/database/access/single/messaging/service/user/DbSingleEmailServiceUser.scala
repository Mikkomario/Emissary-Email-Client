package vf.emissary.database.access.single.messaging.service.user

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.stored.messaging.EmailServiceUser

/**
  * An access point to individual email service users, based on their id
  * @author Mikko Hilpinen
  * @since 22.12.2024, v1.1
  */
case class DbSingleEmailServiceUser(id: Int) 
	extends UniqueEmailServiceUserAccess with SingleIntIdModelAccess[EmailServiceUser]

