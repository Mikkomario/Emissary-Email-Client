-- 
-- Database structure for emissary models
-- Version: v0.1
-- Last generated: 2023-10-13
--

--	Text	----------

-- Represents a character sequence used to separate two statements or parts of a statement
-- text:    The characters that form this delimiter
-- created: Time when this delimiter was added to the database
CREATE TABLE `delimiter`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`text` VARCHAR(2) NOT NULL, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	INDEX d_text_idx (`text`)
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
	CONSTRAINT st_d_delimiter_ref_fk FOREIGN KEY st_d_delimiter_ref_idx (delimiter_id) REFERENCES `delimiter`(`id`) ON DELETE SET NULL
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
-- name:    Human-readable name of this entity, if available
-- created: Time when this address was added to the database
CREATE TABLE `address`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`address` VARCHAR(16) NOT NULL, 
	`name` VARCHAR(16), 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	INDEX ad_address_idx (`address`), 
	INDEX ad_name_idx (`name`)
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a subject or a header given to a sequence of messages
-- author_id: Id of the address / entity that originated this thread
-- created:   Time when this thread was opened
CREATE TABLE `message_thread`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`author_id` INT NOT NULL, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	INDEX mt_created_idx (`created`), 
	CONSTRAINT mt_ad_author_ref_fk FOREIGN KEY mt_ad_author_ref_idx (author_id) REFERENCES `address`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a named subject on a message (thread)
-- author_id: Id of the address / entity that first used this subject
-- created:   Time when this subject was first used
CREATE TABLE `subject`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`author_id` INT NOT NULL, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	CONSTRAINT su_ad_author_ref_fk FOREIGN KEY su_ad_author_ref_idx (author_id) REFERENCES `address`(`id`) ON DELETE CASCADE
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
-- message_id:         Id of the message to which this file is attached
-- original_file_name: Name of the attached file, as it was originally sent
-- stored_file_name:   Name of the attached file, as it appears on the local file system. Empty if identical to the original file name.
CREATE TABLE `attachment`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`message_id` INT NOT NULL, 
	`original_file_name` VARCHAR(24) NOT NULL, 
	`stored_file_name` VARCHAR(24), 
	CONSTRAINT at_m_message_ref_fk FOREIGN KEY at_m_message_ref_idx (message_id) REFERENCES `message`(`id`) ON DELETE CASCADE
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

