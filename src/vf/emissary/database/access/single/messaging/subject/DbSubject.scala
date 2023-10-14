package vf.emissary.database.access.single.messaging.subject

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.util.NotEmpty
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.emissary.database.access.many.messaging.subject.DbSubjects
import vf.emissary.database.access.many.messaging.subject_statement_link.DbSubjectStatementLinks
import vf.emissary.database.access.many.text.statement.DbStatements
import vf.emissary.database.factory.messaging.SubjectFactory
import vf.emissary.database.model.messaging.{SubjectModel, SubjectStatementLinkModel}
import vf.emissary.model.partial.messaging.{SubjectData, SubjectStatementLinkData}
import vf.emissary.model.stored.messaging.Subject

/**
  * Used for accessing individual subjects
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbSubject extends SingleRowModelAccess[Subject] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = SubjectModel
	
	/**
	 * @return Model used for interacting with subject-statement links
	 */
	protected def statementLinkModel = SubjectStatementLinkModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = SubjectFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted subject
	  * @return An access point to that subject
	  */
	def apply(id: Int) = DbSingleSubject(id)
	
	/**
	 * Finds a subject that consists of the specified statements in the specified order
	 * @param statementIds Ids of the statements that form this subject
	 * @param connection Implicit DB connection
	 * @return Subject that matches those statements
	 */
	def findConsistingOf(statementIds: Seq[Int])(implicit connection: Connection) = {
		// Case: Empty subject
		if (statementIds.isEmpty)
			findNotLinkedTo(statementLinkModel.table)
		else {
			// Finds potential subjects and filters them down one statement at a time
			val initialMatchIds = DbSubjectStatementLinks.startingWithStatement(statementIds.head).subjectIds.toSet
			val finalMatchIds = statementIds.zipWithIndex.tail
				.foldLeft(initialMatchIds) { case (potentialMatchIds, (statementId, positionIndex)) =>
					if (potentialMatchIds.isEmpty)
						potentialMatchIds
					else
						DbSubjectStatementLinks.inSubjects(potentialMatchIds)
							.withStatementAtPosition(statementId, positionIndex)
							.subjectIds.toSet
				}
			// Only accepts subjects of specific length
			NotEmpty(finalMatchIds).flatMap { ids => DbSubjects(ids).findShorterThan(statementIds.size + 1).headOption }
		}
	}
	
	/**
	 * Inserts a new subject to the database
	 * @param connection Implicit DB connection
	 * @return Id of the newly inserted subject
	 */
	def newId()(implicit connection: Connection) = model().insert().getInt
	
	/**
	 * Stores a new subject to the database. Avoids inserting duplicates.
	 * @param subject Subject to store (as text)
	 * @param connection Implicit DB connection
	 * @return Either a newly inserted subject (left) or an existing match (right)
	 */
	def store(subject: String)(implicit connection: Connection) = {
		// Stores the statements first
		val statements = DbStatements.store(subject)
		val statementIds = statements.map { _.either.id }
		// Checks whether it is possible that this subject already exists in the DB
		val existingMatch = {
			if (statements.forall { _.isRight })
				findConsistingOf(statementIds)
			else
				None
		}
		// Inserts the subject if it was missing
		existingMatch.toRight {
			val subject = model.insert(SubjectData())
			statementLinkModel.insert(statementIds.zipWithIndex.map { case (statementId, orderIndex) =>
				SubjectStatementLinkData(subject.id, statementId, orderIndex)
			})
			subject
		}
	}
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique subjects.
	  * @return An access point to the subject that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = UniqueSubjectAccess(mergeCondition(condition))
}

