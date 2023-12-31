-- 
-- Database structure for emissary models
-- Version: v0.1
-- Last generated: 2023-10-16
--

CREATE DATABASE IF NOT EXISTS `emissary_db` 
	DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
USE `emissary_db`;

--	Text	----------

-- Represents a character sequence used to separate two statements or parts of a statement
-- text:    The characters that form this delimiter
-- created: Time when this delimiter was added to the database
CREATE TABLE `delimiter`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`text` VARCHAR(2) NOT NULL, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	INDEX de_text_idx (`text`)
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents an individual word used in a text document. Case-sensitive.
-- text:    Text representation of this word
-- created: Time when this word was added to the database
CREATE TABLE `word`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`text` VARCHAR(16) NOT NULL, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	INDEX w_text_idx (`text`)
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents an individual statement made within some text. Consecutive statements form whole texts.
-- delimiter_id: Id of the delimiter that terminates this sentence. None if this sentence is not terminated with any character.
-- created:      Time when this statement was first made
CREATE TABLE `statement`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`delimiter_id` INT, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	INDEX st_created_idx (`created`), 
	CONSTRAINT st_de_delimiter_ref_fk FOREIGN KEY st_de_delimiter_ref_idx (delimiter_id) REFERENCES `delimiter`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Records when a word is used in a statement
-- statement_id: Id of the statement where the referenced word appears
-- word_id:      Id of the word that appears in the described statement
-- order_index:  Index at which the specified word appears within the referenced statement (0-based)
CREATE TABLE `word_placement`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`statement_id` INT NOT NULL, 
	`word_id` INT NOT NULL, 
	`order_index` TINYINT NOT NULL, 
	INDEX wp_order_index_idx (`order_index`), 
	CONSTRAINT wp_st_statement_ref_fk FOREIGN KEY wp_st_statement_ref_idx (statement_id) REFERENCES `statement`(`id`) ON DELETE CASCADE, 
	CONSTRAINT wp_w_word_ref_fk FOREIGN KEY wp_w_word_ref_idx (word_id) REFERENCES `word`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;


--	Messaging	----------

-- Represents an address that represents person or another entity that reads or writes messages.
-- address: A string representation of this address
-- created: Time when this address was added to the database
CREATE TABLE `address`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`address` VARCHAR(16) NOT NULL, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	INDEX ad_address_idx (`address`)
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a subject or a header given to a sequence of messages
-- created: Time when this thread was opened
CREATE TABLE `message_thread`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	INDEX mt_created_idx (`created`)
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a named subject on a message (thread)
-- created: Time when this subject was first used
CREATE TABLE `subject`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Links a human-readable name to an email address
-- address_id:       Id of the address to which this name corresponds
-- name:             Human-readable name of this entity, if available
-- created:          Time when this link was first documented
-- is_self_assigned: Whether this name is used by this person themselves
CREATE TABLE `address_name`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`address_id` INT NOT NULL, 
	`name` VARCHAR(16), 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`is_self_assigned` BOOLEAN NOT NULL DEFAULT FALSE, 
	INDEX an_name_idx (`name`), 
	CONSTRAINT an_ad_address_ref_fk FOREIGN KEY an_ad_address_ref_idx (address_id) REFERENCES `address`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a message sent between two or more individuals or entities
-- thread_id:   Id of the thread to which this message belongs
-- sender_id:   Id of the address from which this message was sent
-- message_id:  (Unique) id given to this message by the sender
-- reply_to_id: Id of the message this message replies to, if applicable
-- created:     Time when this message was sent
CREATE TABLE `message`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`thread_id` INT NOT NULL, 
	`sender_id` INT NOT NULL, 
	`message_id` VARCHAR(16), 
	`reply_to_id` INT, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	INDEX m_message_id_idx (`message_id`), 
	INDEX m_created_idx (`created`), 
	CONSTRAINT m_mt_thread_ref_fk FOREIGN KEY m_mt_thread_ref_idx (thread_id) REFERENCES `message_thread`(`id`) ON DELETE CASCADE, 
	CONSTRAINT m_ad_sender_ref_fk FOREIGN KEY m_ad_sender_ref_idx (sender_id) REFERENCES `address`(`id`) ON DELETE CASCADE, 
	CONSTRAINT m_m_reply_to_ref_fk FOREIGN KEY m_m_reply_to_ref_idx (reply_to_id) REFERENCES `message`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Connects a subject with a message thread in which it was used
-- thread_id:  Id of the thread where the referenced subject was used
-- subject_id: Id of the subject used in the specified thread
-- created:    Time when this subject was first used in the specified thread
CREATE TABLE `message_thread_subject_link`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`thread_id` INT NOT NULL, 
	`subject_id` INT NOT NULL, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	INDEX mtsl_created_idx (`created`), 
	CONSTRAINT mtsl_mt_thread_ref_fk FOREIGN KEY mtsl_mt_thread_ref_idx (thread_id) REFERENCES `message_thread`(`id`) ON DELETE CASCADE, 
	CONSTRAINT mtsl_su_subject_ref_fk FOREIGN KEY mtsl_su_subject_ref_idx (subject_id) REFERENCES `subject`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Connects a message thread subject to the statements made within that subject
-- subject_id:   Id of the described subject
-- statement_id: Id of the statement made within the referenced subject
-- order_index:  Index where this statement appears within the referenced subject (0-based)
CREATE TABLE `subject_statement_link`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`subject_id` INT NOT NULL, 
	`statement_id` INT NOT NULL, 
	`order_index` TINYINT NOT NULL, 
	INDEX ssl_order_index_idx (`order_index`), 
	CONSTRAINT ssl_su_subject_ref_fk FOREIGN KEY ssl_su_subject_ref_idx (subject_id) REFERENCES `subject`(`id`) ON DELETE CASCADE, 
	CONSTRAINT ssl_st_statement_ref_fk FOREIGN KEY ssl_st_statement_ref_idx (statement_id) REFERENCES `statement`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents an attached file within a message
-- message_id: Id of the message to which this file is attached
-- file_name:  Name of the attached file, as appears on the file system
CREATE TABLE `attachment`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`message_id` INT NOT NULL, 
	`file_name` VARCHAR(24) NOT NULL, 
	CONSTRAINT at_m_message_ref_fk FOREIGN KEY at_m_message_ref_idx (message_id) REFERENCES `message`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Links a message to it's assigned recipients
-- message_id:   Id of the sent message
-- recipient_id: Id of the message recipient (address)
-- role_id:      Role / type of the message recipient
-- 		References enumeration RecipientType
-- 		Possible values are: 1 = primary, 2 = copy, 3 = hidden copy
CREATE TABLE `message_recipient_link`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`message_id` INT NOT NULL, 
	`recipient_id` INT NOT NULL, 
	`role_id` TINYINT NOT NULL, 
	CONSTRAINT mrl_m_message_ref_fk FOREIGN KEY mrl_m_message_ref_idx (message_id) REFERENCES `message`(`id`) ON DELETE CASCADE, 
	CONSTRAINT mrl_ad_recipient_ref_fk FOREIGN KEY mrl_ad_recipient_ref_idx (recipient_id) REFERENCES `address`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Documents a statement made within a message
-- message_id:   Id of the message where the statement was made
-- statement_id: The statement that was made
-- order_index:  Index of the statement in the message (0-based)
CREATE TABLE `message_statement_link`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`message_id` INT NOT NULL, 
	`statement_id` INT NOT NULL, 
	`order_index` TINYINT NOT NULL, 
	INDEX msl_order_index_idx (`order_index`), 
	CONSTRAINT msl_m_message_ref_fk FOREIGN KEY msl_m_message_ref_idx (message_id) REFERENCES `message`(`id`) ON DELETE CASCADE, 
	CONSTRAINT msl_st_statement_ref_fk FOREIGN KEY msl_st_statement_ref_idx (statement_id) REFERENCES `statement`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Documents an unresolved reference made from a reply message
-- message_id:            Id of the message from which this reference is made from
-- referenced_message_id: Message id of the referenced message
-- created:               Time when this pending reply reference was added to the database
CREATE TABLE `pending_reply_reference`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`message_id` INT NOT NULL,
	`referenced_message_id` VARCHAR(18) NOT NULL,
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT prr_m_message_ref_fk FOREIGN KEY prr_m_message_ref_idx (message_id) REFERENCES `message`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Used for documenting those message ids involved within threads, that have not been linked to any read message
-- thread_id:             Id of the message thread with which the referenced message is linked to
-- referenced_message_id: Message id belonging to some unread message in the linked thread
-- created:               Time when this pending thread reference was added to the database
CREATE TABLE `pending_thread_reference`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`thread_id` INT NOT NULL,
	`referenced_message_id` VARCHAR(18) NOT NULL,
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT ptr_mt_thread_ref_fk FOREIGN KEY ptr_mt_thread_ref_idx (thread_id) REFERENCES `message_thread`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;


--	Url	----------

-- Represents the address of an internet service
-- url:     Full http(s) address of this domain in string format. Includes protocol, domain name and possible port number.
-- created: Time when this domain was added to the database
CREATE TABLE `domain`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`url` VARCHAR(12) NOT NULL, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a specific http(s) request url, not including any query parameters
-- domain_id: Id of the domain part of this url
-- path:      Part of this url that comes after the domain part. Doesn't include any query parameters, nor the initial forward slash.
-- created:   Time when this request path was added to the database
CREATE TABLE `request_path`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`domain_id` INT NOT NULL, 
	`path` VARCHAR(12), 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	CONSTRAINT rp_do_domain_ref_fk FOREIGN KEY rp_do_domain_ref_idx (domain_id) REFERENCES `domain`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a link for a specific http(s) request
-- request_path_id:  Id of the targeted internet address, including the specific sub-path
-- query_parameters: Specified request parameters in model format
-- created:          Time when this link was added to the database
CREATE TABLE `link`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`request_path_id` INT NOT NULL, 
	`query_parameters` VARCHAR(255), 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	CONSTRAINT l_rp_request_path_ref_fk FOREIGN KEY l_rp_request_path_ref_idx (request_path_id) REFERENCES `request_path`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Places a link within a statement
-- statement_id: Id of the statement where the specified link is referenced
-- link_id:      Referenced link
-- order_index:  Index where the link appears in the statement (0-based)
CREATE TABLE `link_placement`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`statement_id` INT NOT NULL, 
	`link_id` INT NOT NULL, 
	`order_index` TINYINT NOT NULL, 
	CONSTRAINT lp_st_statement_ref_fk FOREIGN KEY lp_st_statement_ref_idx (statement_id) REFERENCES `statement`(`id`) ON DELETE CASCADE, 
	CONSTRAINT lp_l_link_ref_fk FOREIGN KEY lp_l_link_ref_idx (link_id) REFERENCES `link`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

