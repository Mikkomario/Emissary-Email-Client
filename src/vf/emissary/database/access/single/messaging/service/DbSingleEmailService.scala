package vf.emissary.database.access.single.messaging.service

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.stored.messaging.EmailService

/**
  * An access point to individual email services, based on their id
  * @author Mikko Hilpinen
  * @since 22.12.2024, v1.1
  */
case class DbSingleEmailService(id: Int) 
	extends UniqueEmailServiceAccess with SingleIntIdModelAccess[EmailService]

