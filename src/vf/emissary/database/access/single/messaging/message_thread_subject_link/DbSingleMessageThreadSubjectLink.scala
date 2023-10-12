package vf.emissary.database.access.single.messaging.message_thread_subject_link

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.model.stored.messaging.MessageThreadSubjectLink

/**
  * An access point to individual message thread subject links, based on their id
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
case class DbSingleMessageThreadSubjectLink(id: Int) 
	extends UniqueMessageThreadSubjectLinkAccess with SingleIntIdModelAccess[MessageThreadSubjectLink]

