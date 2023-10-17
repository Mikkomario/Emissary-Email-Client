package vf.emissary.database.factory.messaging

import utopia.vault.nosql.factory.row.linked.CombiningFactory
import vf.emissary.model.combined.messaging.ThreadSubject
import vf.emissary.model.stored.messaging.{MessageThreadSubjectLink, Subject}

/**
  * Used for reading thread subjects from the database
  * @author Mikko Hilpinen
  * @since 17.10.2023, v0.1
  */
object ThreadSubjectFactory extends CombiningFactory[ThreadSubject, Subject, MessageThreadSubjectLink]
{
	// IMPLEMENTED	--------------------
	
	override def childFactory = MessageThreadSubjectLinkFactory
	
	override def parentFactory = SubjectFactory
	
	override def apply(subject: Subject, threadLink: MessageThreadSubjectLink) = 
		ThreadSubject(subject, threadLink)
}

