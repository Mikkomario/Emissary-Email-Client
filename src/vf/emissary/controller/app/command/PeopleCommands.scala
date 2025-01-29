package vf.emissary.controller.app.command

import utopia.flow.collection.immutable.Single
import utopia.flow.util.console.{ArgumentSchema, Command}
import utopia.flow.view.template.Extender
import vf.emissary.database.access.many.messaging.address.DbNamedAddresses
import vf.emissary.database.EmissaryContext._

/**
 * Contains commands for interacting with addresses and people entries
 *
 * @author Mikko Hilpinen
 * @since 27.12.2024, v1.1
 */
object PeopleCommands extends Extender[Seq[Command]]
{
	// ATTRIBUTES   -----------------------
	
	private val addressCommand = Command("whois", "address")(
		ArgumentSchema("name", help = "Name of the person you're searching for, or part of their email address")) {
		args =>
			args("name").string match {
				case Some(name) =>
					connectionPool.logging { implicit c =>
						val results = DbNamedAddresses.withNameOrAddressLike(name).pull.sorted
						if (results.isEmpty)
							println(s"No name or address matches '$name'")
						else {
							println(s"Found ${results.size} addresses that match '$name':")
							results.foreach { a => println(s"\t- ${a.address.address}: ${ a.names.mkString(" / ") }") }
						}
					}
				case None => println("Missing required argument 'name'")
			}
	}
	
	override val wrapped: Seq[Command] = Single(addressCommand)
}
