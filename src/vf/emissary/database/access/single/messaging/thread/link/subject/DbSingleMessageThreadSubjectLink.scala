package vf.emissary.database.access.single.messaging.thread.link.subject

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.stored.messaging.MessageThreadSubjectLink

/**
  * An access point to individual message thread subject links, based on their id
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
case class DbSingleMessageThreadSubjectLink(id: Int) 
	extends UniqueMessageThreadSubjectLinkAccess with SingleIntIdModelAccess[MessageThreadSubjectLink]

