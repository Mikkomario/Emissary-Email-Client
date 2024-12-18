package vf.emissary.database.factory.messaging

import utopia.vault.nosql.factory.row.linked.CombiningFactory
import vf.emissary.model.combined.messaging.ThreadSubject
import vf.emissary.model.stored.messaging.{MessageThreadSubjectLink, Subject}

/**
  * Used for reading thread subjects from the database
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
object ThreadSubjectDbFactory extends CombiningFactory[ThreadSubject, Subject, MessageThreadSubjectLink]
{
	// IMPLEMENTED	--------------------
	
	override def childFactory = MessageThreadSubjectLinkDbFactory
	
	override def parentFactory = SubjectDbFactory
	
	/**
	  * @param subject subject to wrap
	  * @param threadLink thread link to attach to this subject
	  */
	override def apply(subject: Subject, threadLink: MessageThreadSubjectLink) = 
		ThreadSubject(subject, threadLink)
}

