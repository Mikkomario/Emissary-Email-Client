package vf.emissary.database.access.single.messaging.thread

import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.emissary.database.access.many.messaging.thread.link.subject.DbMessageThreadSubjectLinks
import vf.emissary.database.access.single.messaging.thread.link.subject.DbMessageThreadSubjectLink
import vf.emissary.model.stored.messaging.MessageThread

/**
  * An access point to individual message threads, based on their id
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
case class DbSingleMessageThread(id: Int) 
	extends UniqueMessageThreadAccess with SingleIntIdModelAccess[MessageThread]
{
	/**
	 * @return Access to subject links concerning this thread
	 */
	def subjectLinks = DbMessageThreadSubjectLinks.inThread(id)
	
	/**
	 * Assigns a subject to this thread, unless recorded already
	 * @param subjectId Id of the subject to assign
	 * @param connection Implicit DB connection
	 * @return Either a newly inserted link id (left), or id of the existing link (right)
	 */
	def assignSubject(subjectId: Int)(implicit connection: Connection) =
		DbMessageThreadSubjectLink.between(id, subjectId).pullOrInsertId()
}