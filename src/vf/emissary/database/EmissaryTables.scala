package vf.emissary.database

import utopia.vault.model.immutable.Table

/**
  * Used for accessing the database tables introduced in this project
  * @author Mikko Hilpinen
  * @since 12.10.2023, v0.1
  */
object EmissaryTables
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Table that contains attachments (Represents an attached file within a message)
	  */
	lazy val attachment = apply("attachment")
	/**
	 * Table that contains attachment message links (Links an attachment to the messages in which it
	 * appears)
	 */
	lazy val attachmentMessageLink = apply("attachment_message_link")
	
	/**
	  * Table that contains email services (Represents a server / service which manages emails)
	  */
	lazy val emailService = apply("email_service")
	/**
	  * Table that contains email service users (Represents a user of a specific emailing service)
	  */
	lazy val emailServiceUser = apply("email_service_user")
	
	/**
	  * Table that contains addresses (Represents an address that represents person or another entity that reads
	  * or writes messages.)
	  */
	lazy val address = apply("address")
	/**
	  * Table that contains address names (Links a human-readable name to an email address)
	  */
	lazy val addressName = apply("address_name")
	
	/**
	  * Table that contains messages (Represents a message sent between two or more individuals or entities)
	  */
	lazy val message = apply("message")
	/**
	  * Table that contains message statement links (Documents a statement made within a message)
	  */
	lazy val messageStatementLink = apply("message_statement_link")
	/**
	  * Table that contains message recipient links (Links a message to it's assigned recipients)
	  */
	lazy val messageRecipientLink = apply("message_recipient_link")
	
	/**
	  * Table that contains message threads (Represents a subject or a header given to a sequence of messages)
	  */
	lazy val messageThread = apply("message_thread")
	/**
	  * Table that contains message thread subject links (Connects a subject 
	  * with a message thread in which it was used)
	  */
	lazy val messageThreadSubjectLink = apply("message_thread_subject_link")
	
	/**
	  * Table that contains pending reply references (Documents an unresolved reference made from a reply message)
	  */
	lazy val pendingReplyReference = apply("pending_reply_reference")
	/**
	  * Table that contains pending thread references (Used for documenting those message ids involved within threads,
	  * that have not been linked to any read message)
	  */
	lazy val pendingThreadReference = apply("pending_thread_reference")
	
	/**
	  * Table that contains subjects (Represents a named subject on a message (thread))
	  */
	lazy val subject = apply("subject")
	/**
	  * Table that contains subject statement links (Connects a message thread subject to the statements made
	  * within that subject)
	  */
	lazy val subjectStatementLink = apply("subject_statement_link")
	
	
	// OTHER	--------------------
	
	private def apply(tableName: String): Table = EmissaryContext.table(tableName)
}

