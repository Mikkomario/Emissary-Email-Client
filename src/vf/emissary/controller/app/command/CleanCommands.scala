package vf.emissary.controller.app.command

import utopia.flow.collection.immutable.Single
import utopia.flow.util.console.{ArgumentSchema, Command}
import utopia.flow.view.template.Extender
import vf.emissary.controller.archive.CleanArchives
import vf.emissary.util.Common._

/**
 * Provides interactive console commands for cleaning message archives
 * @author Mikko Hilpinen
 * @since 27.12.2024, v1.1
 */
object CleanCommands extends Extender[Seq[Command]]
{
	// ATTRIBUTES   -------------------------
	
	private val cleanCommand = Command("clean", help = "Cleans a message thread, removing duplicate text entries")(
		ArgumentSchema("threadId", help = "Id of the targeted thread")) {
		args =>
			args("threadId").int match {
				case Some(threadId) =>
					cPool.logging { implicit c =>
						val cleanCount = CleanArchives.removeDuplicateTextWithinThread(threadId)
						if (cleanCount == 0)
							println("The targeted thread was not affected")
						else
							println(s"Removed $cleanCount duplicate statements from this thread")
					}
				
				case None => println("Required parameter 'threadId' is missing")
			}
	}
	
	override def wrapped: Seq[Command] = Single(cleanCommand)
}
