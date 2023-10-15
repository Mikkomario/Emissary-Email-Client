package vf.emissary.database.access.many.text.statement

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.view.UnconditionalView
import vf.emissary.database.access.many.text.delimiter.DbDelimiters
import vf.emissary.database.access.many.text.word.DbWords
import vf.emissary.database.access.single.text.statement.DbStatement
import vf.emissary.model.stored.text.Delimiter

import scala.collection.immutable.VectorBuilder

/**
  * The root access point when targeting multiple statements at a time
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object DbStatements extends ManyStatementsAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted statements
	  * @return An access point to statements with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbStatementsSubset(ids)
	
	/**
	 * Stores the specified text to the database as a sequence of statements.
	 * Avoids inserting duplicate entries.
	 * @param text Text to store as statements
	 * @param connection Implicit DB connection
	 * @return Stored statements, where each entry is either right, if it existed already, or left,
	 *         if it was newly inserted
	 */
	def store(text: String)(implicit connection: Connection) = {
		val parts = Delimiter.anyDelimiterRegex.divide(text).filterNot { _.either.isEmpty }
		val dataBuilder = new VectorBuilder[(String, String)]()
		var nextStartIndex = 0
		while (nextStartIndex < parts.size) {
			// Finds the next delimiter
			(nextStartIndex until parts.size).find { parts(_).isRight } match {
				// Case: Next delimiter found => Collects remaining delimiter and extracts text part
				case Some(delimiterStartIndex) =>
					val delimiterParts = parts.drop(delimiterStartIndex).takeWhile { _.isRight }.map { _.either }
					val delimiterText = delimiterParts.mkString
					val wordsText = parts.slice(nextStartIndex, delimiterStartIndex).mkString
					dataBuilder += wordsText -> delimiterText
					nextStartIndex = delimiterStartIndex + delimiterParts.size
				// Case: No delimiter found => Extracts text part without delimiter
				case None =>
					dataBuilder += (parts.drop(nextStartIndex).map { _.either }.mkString -> "")
					nextStartIndex = parts.size
			}
		}
		val data = dataBuilder.result()
		// Stores the delimiters first
		val delimiterMap = DbDelimiters.store(data.map { _._2 }.toSet.filterNot { _.isEmpty })
		// Next stores the words and the statements
		val statementWordData = data.map { case (wordsPart, delimiterPart) =>
			wordsPart.split(' ').toVector -> delimiterMap.get(delimiterPart)
		}
		val wordMap = DbWords.store(statementWordData.flatMap { _._1 }.toSet)
		statementWordData.map { case (words, delimiterId) =>
			DbStatement.store(words.map(wordMap.apply), delimiterId)
		}
	}
	
	
	// NESTED	--------------------
	
	class DbStatementsSubset(targetIds: Set[Int]) extends ManyStatementsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(index in targetIds)
	}
}

