package vf.emissary.database.factory.messaging

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.util.TryExtensions._
import utopia.vault.error.HandleError
import utopia.vault.model.enumeration.SelectTarget
import utopia.vault.model.immutable.{Result, Table}
import utopia.vault.nosql.factory.FromResultFactory
import utopia.vault.sql.{JoinType, OrderBy}
import vf.emissary.model.combined.messaging.DetailedEmailServiceUser

/**
 * Used for pulling email service user data from the DB, including service, address and name information.
 *
 * @author Mikko Hilpinen
 * @since 22.12.2024, v1.1
 */
object DetailedEmailServiceUserDbFactory extends FromResultFactory[DetailedEmailServiceUser]
{
	// ATTRIBUTES   ----------------------
	
	private val user = EmailServiceUserDbFactory
	private val service = EmailServiceDbFactory
	private val address = NamedAddressDbFactory
	
	override lazy val joinedTables: Seq[Table] = service.table +: address.tables
	override val joinType: JoinType = JoinType.Left
	
	override lazy val selectTarget: SelectTarget = tables
	
	override val defaultOrdering: Option[OrderBy] = None
	
	
	// IMPLEMENTED  ----------------------
	
	override def table: Table = user.table
	
	override def apply(result: Result): Seq[DetailedEmailServiceUser] = result
		.groupAnd(user) { (user, rows) =>
			service(rows.head)
				.map { service =>
					val parsedAddress = address(Result(rows)).head
					Some(DetailedEmailServiceUser(user, service, parsedAddress))
				}
				.getOrMap { error =>
					HandleError.duringRowParsing(error)
					None
				}
		}.view.flatten.toOptimizedSeq
}
