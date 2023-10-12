package vf.emissary.database

import utopia.vault.database.Tables
import utopia.vault.model.immutable.Table

import vf.emissary.util.Common._

/**
  * Used for accessing the database tables introduced in this project
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object EmissaryTables extends Tables(cPool)
{
	// ATTRIBUTES   ----------------
	
	private val databaseName = "emissary_db"
	
	
	// COMPUTED	--------------------
	
	/**
	  * Table that contains addresses (Represents an address that represents person or another entity that reads
	  *  or writes messages.)
	  */
	def address = apply("address")
	
	/**
	  * Table that contains delimiters (Represents a character sequence used to separate two statements or parts
	  *  of a statement)
	  */
	def delimiter = apply("delimiter")
	
	/**
	  * Table that contains messages (Represents a message sent between two or more individuals or entities)
	  */
	def message = apply("message")
	
	/**
	  * Table that contains message statement links (Documents a statement made within a message)
	  */
	def messageStatementLink = apply("message_statement_link")
	
	/**
	  * Table that contains message threads (Represents a subject or a header given to a sequence of messages)
	  */
	def messageThread = apply("message_thread")
	
	/**
	  * Table that contains message thread subject links (Connects a subject 
	  * with a message thread in which it was used)
	  */
	def messageThreadSubjectLink = apply("message_thread_subject_link")
	
	/**
	  * Table that contains statements (Represents an individual statement made within some text.
	  *  Consecutive statements form whole texts.)
	  */
	def statement = apply("statement")
	
	/**
	  * Table that contains subjects (Represents a named subject on a message (thread))
	  */
	def subject = apply("subject")
	
	/**
	  * Table that contains subject statement links (Connects a message thread subject to the statements made
	  *  within that subject)
	  */
	def subjectStatementLink = apply("subject_statement_link")
	
	/**
	  * Table that contains words (Represents an individual word used in a text document. Case-sensitive.)
	  */
	def word = apply("word")
	
	/**
	  * Table that contains word placements (Records when a word is used in a statement)
	  */
	def wordPlacement = apply("word_placement")
	
	
	// OTHER	--------------------
	
	private def apply(tableName: String): Table = apply(databaseName, tableName)
}

