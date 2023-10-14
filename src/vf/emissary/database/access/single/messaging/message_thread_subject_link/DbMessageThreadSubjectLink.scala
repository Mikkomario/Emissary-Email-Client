package vf.emissary.database.access.single.messaging.message_thread_subject_link

import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{SubView, UnconditionalView, View}
import utopia.vault.sql.Condition
import vf.emissary.database.factory.messaging.MessageThreadSubjectLinkFactory
import vf.emissary.database.model.messaging.MessageThreadSubjectLinkModel
import vf.emissary.model.stored.messaging.MessageThreadSubjectLink

/**
  * Used for accessing individual message thread subject links
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbMessageThreadSubjectLink 
	extends SingleRowModelAccess[MessageThreadSubjectLink] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = MessageThreadSubjectLinkModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = MessageThreadSubjectLinkFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted message thread subject link
	  * @return An access point to that message thread subject link
	  */
	def apply(id: Int) = DbSingleMessageThreadSubjectLink(id)
	
	/**
	 * @param threadId Id of the targeted thread
	 * @param subjectId Id of the targeted subject
	 * @return Access to a link between those two
	 */
	def between(threadId: Int, subjectId: Int) = new DbSpecificThreadSubjectLink(threadId, subjectId)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique message thread subject links.
	  * @return An access point to the message thread subject link that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = 
		UniqueMessageThreadSubjectLinkAccess(mergeCondition(condition))
	
	
	// NESTED   -----------------------
	
	class DbSpecificThreadSubjectLink(threadId: Int, subjectId: Int)
		extends UniqueMessageThreadSubjectLinkAccess with SubView
	{
		// ATTRIBUTES   ---------------
		
		private lazy val conditionModel = model.withThreadId(threadId).withSubjectId(subjectId)
		
		
		// IMPLEMENTED  ---------------
		
		override protected def parent: View = DbMessageThreadSubjectLink
		
		override def filterCondition: Condition = conditionModel.toCondition
		
		
		// OTHER    ------------------
		
		/**
		 * Finds the id of this link. Inserts a new link if not already found from the DB.
		 * @param connection Implicit DB connection
		 * @return Either a newly inserted link id (left), or an existing id (right)
		 */
		def pullOrInsertId()(implicit connection: Connection) =
			id.toRight { conditionModel.insert().getInt }
	}
}

