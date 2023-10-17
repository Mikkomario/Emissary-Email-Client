package vf.emissary.test

import utopia.vault.database.Connection
import vf.emissary.database.access.single.messaging.message_thread.DbMessageThread
import vf.emissary.util.Common._

/**
 * Tests sender subject-finding
 * @author Mikko Hilpinen
 * @since 17.10.2023, v0.1
 */
object FindSubjectTest extends App
{
	cPool { implicit c =>
		Connection.debugPrintsEnabled = true
		println(DbMessageThread.findIdForSentSubject(22, 4))
		Connection.debugPrintsEnabled = false
	}
}
