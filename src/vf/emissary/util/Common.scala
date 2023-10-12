package vf.emissary.util

import utopia.flow.async.context.ThreadPool
import utopia.flow.util.logging.{Logger, SysErrLogger}
import utopia.vault.database.ConnectionPool

import scala.concurrent.ExecutionContext

/**
 * Contains commonly used (static) values
 * @author Mikko Hilpinen
 * @since 12.10.2023, v0.1
 */
object Common
{
	/**
	 * Implicitly used logging implementation
	 */
	implicit val log: Logger = SysErrLogger
	/**
	 * Implicitly used (thread) execution context
	 */
	implicit val exc: ExecutionContext = new ThreadPool("Emissary")
	/**
	 * Implicitly used database connection pool
	 */
	implicit val cPool: ConnectionPool = new ConnectionPool()
}
