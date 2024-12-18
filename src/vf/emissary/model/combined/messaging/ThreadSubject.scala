package vf.emissary.model.combined.messaging

import utopia.flow.view.template.Extender
import utopia.vault.model.template.HasId
import vf.emissary.model.factory.messaging.SubjectFactoryWrapper
import vf.emissary.model.partial.messaging.SubjectData
import vf.emissary.model.stored.messaging.{MessageThreadSubjectLink, Subject}

object ThreadSubject
{
	// OTHER	--------------------
	
	/**
	  * @param subject subject to wrap
	  * @param threadLink thread link to attach to this subject
	  * @return Combination of the specified subject and thread link
	  */
	def apply(subject: Subject, threadLink: MessageThreadSubjectLink): ThreadSubject = 
		_ThreadSubject(subject, threadLink)
	
	
	// NESTED	--------------------
	
	/**
	  * @param subject subject to wrap
	  * @param threadLink thread link to attach to this subject
	  */
	private case class _ThreadSubject(subject: Subject, threadLink: MessageThreadSubjectLink) 
		extends ThreadSubject
	{
		// IMPLEMENTED	--------------------
		
		override protected def wrap(factory: Subject) = copy(subject = factory)
	}
}

/**
  * Represents a message (thread) subject
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait ThreadSubject 
	extends Extender[SubjectData] with HasId[Int] with SubjectFactoryWrapper[Subject, ThreadSubject]
{
	// ABSTRACT	--------------------
	
	/**
	  * Wrapped subject
	  */
	def subject: Subject
	/**
	  * The thread link that is attached to this subject
	  */
	def threadLink: MessageThreadSubjectLink
	
	
	// IMPLEMENTED	--------------------
	
	/**
	  * Id of this subject in the database
	  */
	override def id = subject.id
	
	override def wrapped = subject.data
	override protected def wrappedFactory = subject
}
