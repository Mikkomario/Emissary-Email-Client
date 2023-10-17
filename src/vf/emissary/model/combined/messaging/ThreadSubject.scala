package vf.emissary.model.combined.messaging

import utopia.flow.view.template.Extender
import vf.emissary.model.partial.messaging.SubjectData
import vf.emissary.model.stored.messaging.{MessageThreadSubjectLink, Subject}

/**
  * Represents a thread-specific subject
  * @author Mikko Hilpinen
  * @since 17.10.2023, v0.1
  */
case class ThreadSubject(subject: Subject, threadLink: MessageThreadSubjectLink) extends Extender[SubjectData]
{
	// COMPUTED	--------------------
	
	/**
	  * Id of this subject in the database
	  */
	def id = subject.id
	
	
	// IMPLEMENTED	--------------------
	
	override def wrapped = subject.data
}

