-- 
-- Database structure for emissary models
-- Version: v1.1
-- Last generated: 2026-05-01
--

--	Messaging	----------

-- Represents an address that represents person or another entity that reads or writes messages.
-- address: A string representation of this address
-- created: Time when this address was added to the database
CREATE TABLE `address`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`address` VARCHAR(16) NOT NULL, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	INDEX ems_ad_address_idx (`address`)
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents an attached file within a message
-- relative_path: Name of the attached file, as appears on the file system
-- size:          Size of this attachment in bytes
CREATE TABLE `attachment`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`relative_path` VARCHAR(32), 
	`size` BIGINT NOT NULL, 
	INDEX ems_at_combo_1_idx (relative_path, size)
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a server / service which manages emails
-- address: Connection address of this (IMAP/SMTP) service
-- created: Time when this email service was added to the database
-- name:    Name of this email service. Empty if not defined.
CREATE TABLE `email_service`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`address` VARCHAR(16) NOT NULL, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`name` VARCHAR(16)
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a subject or a header given to a sequence of messages
-- created: Time when this thread was opened
CREATE TABLE `message_thread`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	INDEX ems_mt_created_idx (`created`)
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a named subject on a message (thread)
-- created: Time when this subject was first used
CREATE TABLE `subject`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	INDEX ems_su_created_idx (`created`)
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
	INDEX ems_an_name_idx (`name`), 
	CONSTRAINT ems_an_ad_address_ref_fk FOREIGN KEY ems_an_ad_address_ref_idx (address_id) REFERENCES `address`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a user of a specific emailing service
-- service_id: Id of the used emailing service
-- address_id: Email address that represents this user
-- password:   Password used for authenticating to the email service. Empty if password should be provided externally.
-- created:    Time when this email service user was added to the database
CREATE TABLE `email_service_user`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`service_id` INT NOT NULL, 
	`address_id` INT NOT NULL, 
	`password` VARCHAR(16), 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	CONSTRAINT ems_esu_es_service_ref_fk FOREIGN KEY ems_esu_es_service_ref_idx (service_id) REFERENCES `email_service`(`id`) ON DELETE CASCADE, 
	CONSTRAINT ems_esu_ad_address_ref_fk FOREIGN KEY ems_esu_ad_address_ref_idx (address_id) REFERENCES `address`(`id`) ON DELETE CASCADE
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
	INDEX ems_m_message_id_idx (`message_id`), 
	INDEX ems_m_created_idx (`created`), 
	CONSTRAINT ems_m_mt_thread_ref_fk FOREIGN KEY ems_m_mt_thread_ref_idx (thread_id) REFERENCES `message_thread`(`id`) ON DELETE CASCADE, 
	CONSTRAINT ems_m_ad_sender_ref_fk FOREIGN KEY ems_m_ad_sender_ref_idx (sender_id) REFERENCES `address`(`id`) ON DELETE CASCADE, 
	CONSTRAINT ems_m_m_reply_to_ref_fk FOREIGN KEY ems_m_m_reply_to_ref_idx (reply_to_id) REFERENCES `message`(`id`) ON DELETE SET NULL
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
	INDEX ems_mtsl_created_idx (`created`), 
	CONSTRAINT ems_mtsl_mt_thread_ref_fk FOREIGN KEY ems_mtsl_mt_thread_ref_idx (thread_id) REFERENCES `message_thread`(`id`) ON DELETE CASCADE, 
	CONSTRAINT ems_mtsl_su_subject_ref_fk FOREIGN KEY ems_mtsl_su_subject_ref_idx (subject_id) REFERENCES `subject`(`id`) ON DELETE CASCADE
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
	CONSTRAINT ems_ptr_mt_thread_ref_fk FOREIGN KEY ems_ptr_mt_thread_ref_idx (thread_id) REFERENCES `message_thread`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Connects a message thread subject to the statements made within that subject
-- subject_id:   Id of the described subject
-- statement_id: Id of the placed statement
-- order_index:  0-based index that indicates the specific location of the placed text
CREATE TABLE `subject_statement_link`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`subject_id` INT NOT NULL, 
	`statement_id` INT NOT NULL, 
	`order_index` TINYINT NOT NULL DEFAULT 0, 
	CONSTRAINT ems_ssl_su_subject_ref_fk FOREIGN KEY ems_ssl_su_subject_ref_idx (subject_id) REFERENCES `subject`(`id`) ON DELETE CASCADE, 
	CONSTRAINT ems_ssl_st_statement_ref_fk FOREIGN KEY ems_ssl_st_statement_ref_idx (statement_id) REFERENCES `statement`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Links an attachment to the messages in which it appears
-- attachment_id: Id of the linked attachment
-- message_id:    Id of the message in which the attachment appears
CREATE TABLE `attachment_message_link`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`attachment_id` INT NOT NULL, 
	`message_id` INT NOT NULL, 
	CONSTRAINT ems_aml_at_attachment_ref_fk FOREIGN KEY ems_aml_at_attachment_ref_idx (attachment_id) REFERENCES `attachment`(`id`) ON DELETE CASCADE, 
	CONSTRAINT ems_aml_m_message_ref_fk FOREIGN KEY ems_aml_m_message_ref_idx (message_id) REFERENCES `message`(`id`) ON DELETE CASCADE
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
	CONSTRAINT ems_mrl_m_message_ref_fk FOREIGN KEY ems_mrl_m_message_ref_idx (message_id) REFERENCES `message`(`id`) ON DELETE CASCADE, 
	CONSTRAINT ems_mrl_ad_recipient_ref_fk FOREIGN KEY ems_mrl_ad_recipient_ref_idx (recipient_id) REFERENCES `address`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Documents a statement made within a message
-- message_id:   Id of the message where the statement was made
-- statement_id: Id of the placed statement
-- order_index:  0-based index that indicates the specific location of the placed text
CREATE TABLE `message_statement_link`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`message_id` INT NOT NULL, 
	`statement_id` INT NOT NULL, 
	`order_index` TINYINT NOT NULL DEFAULT 0, 
	CONSTRAINT ems_msl_m_message_ref_fk FOREIGN KEY ems_msl_m_message_ref_idx (message_id) REFERENCES `message`(`id`) ON DELETE CASCADE, 
	CONSTRAINT ems_msl_st_statement_ref_fk FOREIGN KEY ems_msl_st_statement_ref_idx (statement_id) REFERENCES `statement`(`id`) ON DELETE CASCADE
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
	CONSTRAINT ems_prr_m_message_ref_fk FOREIGN KEY ems_prr_m_message_ref_idx (message_id) REFERENCES `message`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

