package vf.emissary.database

import utopia.bunnymunch.jawn.JsonBunny
import utopia.flow.parse.file.FileExtensions._
import utopia.flow.parse.json.JsonParser
import utopia.flow.util.logging.{Logger, SysErrLogger}
import utopia.logos.database.LogosContext
import utopia.vault.context.{VaultContext, VaultContextWrapper}
import utopia.vault.database.{ConnectionPool, Tables}

import java.nio.file.Path
import scala.concurrent.ExecutionContext

/**
 * Provides access to generally required values, such as database table access and connection pool.
 *
 * @author Mikko Hilpinen
 * @since 29.01.2025, v1.1
 */
object EmissaryContext extends VaultContextWrapper
{
	// ATTRIBUTES   ----------------------
	
	private var _wrapped: Option[VaultContext] = None
	private var _log: Logger = SysErrLogger
	private var _attachmentsDirectory: Option[Path] = None
	
	implicit val jsonParser: JsonParser = JsonBunny
	
	
	// COMPUTED --------------------------
	
	/**
	 * @return Logging implementation to use in Emissary
	 */
	implicit def log: Logger = _log
	
	/**
	 * @return Whether the use of attachments is enabled
	 */
	def attachmentsEnabled = _attachmentsDirectory.isDefined
	/**
	 * @return Directory where email attachments are stored.
	 *         None if attachment-storing is not enabled.
	 */
	def attachmentsDirectory = _attachmentsDirectory
	
	
	// IMPLEMENTED  ----------------------
	
	override protected def wrapped: VaultContext =
		_wrapped.getOrElse { throw new IllegalStateException("EmissaryContext has not been set up yet") }
	
	
	// OTHER    -------------------------
	
	/**
	 * Sets up this context. Also initializes [[LogosContext]].
	 * @param vaultContext A context that provides the database interaction properties for this interface
	 * @param attachmentsDirectory Directory where attachments should be stored.
	 *                             None if attachment-storing is disabled (default).
	 * @param logger Logging implementation to use. Default = System.err.
	 */
	def setupWrapping(vaultContext: VaultContext, attachmentsDirectory: Option[Path] = None,
	                  logger: Logger = SysErrLogger) =
	{
		_wrapped = Some(vaultContext)
		_attachmentsDirectory = attachmentsDirectory
		_log = logger
		LogosContext.setup(vaultContext, jsonParser, logger)
	}
	/**
	 * Sets up this context. Also initializes [[LogosContext]].
	 * @param exc Execution context to use for asynchronous operations
	 * @param cPool Connection pool to use in order to acquire database connections
	 * @param databaseName Name of the database that contains the Logos tables
	 * @param tables The interface for accessing the tables within this project
	 * @param attachmentsDirectory Directory where attachments should be stored.
	 *                             None if attachment-storing is disabled (default).
	 * @param logger Logging implementation to use. Default = System.err.
	 */
	def setup(exc: ExecutionContext, cPool: ConnectionPool, databaseName: String, tables: Tables,
	          attachmentsDirectory: Option[Path] = None, logger: Logger = SysErrLogger): Unit =
		setupWrapping(VaultContext(exc, cPool, databaseName, tables), attachmentsDirectory, logger)
}
