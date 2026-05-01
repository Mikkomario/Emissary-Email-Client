# Emissary
Version: **v1.1**  
Updated: 2026-05-01

## Table of Contents
- [Enumerations](#enumerations)
  - [Recipient Type](#recipient-type)
- [Packages & Classes](#packages-and-classes)
  - [Messaging](#messaging)
    - [Address](#address)
    - [Address Name](#address-name)
    - [Attachment](#attachment)
    - [Attachment Message Link](#attachment-message-link)
    - [Email Service](#email-service)
    - [Email Service User](#email-service-user)
    - [Message](#message)
    - [Message Recipient Link](#message-recipient-link)
    - [Message Statement Link](#message-statement-link)
    - [Message Thread](#message-thread)
    - [Message Thread Subject Link](#message-thread-subject-link)
    - [Pending Reply Reference](#pending-reply-reference)
    - [Pending Thread Reference](#pending-thread-reference)
    - [Subject](#subject)
    - [Subject Statement Link](#subject-statement-link)
  - [Text](#text)
    - [Statement Placement](#statement-placement)
    - [Text Placement](#text-placement)

## Enumerations
Below are listed all enumerations introduced in Emissary, in alphabetical order  

### Recipient Type
Represents the role of a message recipient

Key: `id: Int`  
Default Value: **Primary**

**Values:**
- **Primary** (1) - Represents a primary recipient of a message
- **Copy** (2) - Represents an additional (secondary) recipient of a message
- **Hidden Copy** (3) - Represents a recipient of a message not visible to other recipients

Utilized by the following 1 classes:
- [Message Recipient Link](#message-recipient-link)

## Packages and Classes
Below are listed all classes introduced in Emissary, grouped by package and in alphabetical order.  
There are a total number of 2 packages and 17 classes

### Messaging
This package contains the following 15 classes: [Address](#address), [Address Name](#address-name), [Attachment](#attachment), [Attachment Message Link](#attachment-message-link), [Email Service](#email-service), [Email Service User](#email-service-user), [Message](#message), [Message Recipient Link](#message-recipient-link), [Message Statement Link](#message-statement-link), [Message Thread](#message-thread), [Message Thread Subject Link](#message-thread-subject-link), [Pending Reply Reference](#pending-reply-reference), [Pending Thread Reference](#pending-thread-reference), [Subject](#subject), [Subject Statement Link](#subject-statement-link)

#### Address
Represents an address that represents person or another entity that reads or writes messages.

##### Details
- Combines with possibly multiple [Address Names](#address-name), creating a **Named Address**
- Uses **index**: `address`

##### Properties
Address contains the following 2 properties:
- **Address** - `address: String` - A string representation of this address
- **Created** - `created: Instant` - Time when this address was added to the database

##### Referenced from
- [Address Name](#address-name).`addressId`
- [Email Service User](#email-service-user).`addressId`
- [Message](#message).`senderId`
- [Message Recipient Link](#message-recipient-link).`recipientId`

#### Address Name
Links a human-readable name to an email address

##### Details
- Uses **index**: `name`

##### Properties
Address Name contains the following 4 properties:
- **Address Id** - `addressId: Int` - Id of the address to which this name corresponds
  - Refers to [Address](#address)
- **Name** - `name: String` - Human-readable name of this entity, if available
- **Created** - `created: Instant` - Time when this link was first documented
- **Is Self Assigned** - `isSelfAssigned: Boolean` - Whether this name is used by this person themselves

#### Attachment
Represents an attached file within a message

##### Details
- Combines with [Attachment Message Link](#attachment-message-link), creating a **Message Attachment**
- Uses a **combo index**: `relative_path` => `size`

##### Properties
Attachment contains the following 2 properties:
- **Relative Path** - `relativePath: Path` - Name of the attached file, as appears on the file system
- **Size** - `size: Long` - Size of this attachment in bytes

##### Referenced from
- [Attachment Message Link](#attachment-message-link).`attachmentId`

#### Attachment Message Link
Links an attachment to the messages in which it appears

##### Details

##### Properties
Attachment Message Link contains the following 2 properties:
- **Attachment Id** - `attachmentId: Int` - Id of the linked attachment
  - Refers to [Attachment](#attachment)
- **Message Id** - `messageId: Int` - Id of the message in which the attachment appears
  - Refers to [Message](#message)

#### Email Service
Represents a server / service which manages emails

##### Details

##### Properties
Email Service contains the following 3 properties:
- **Address** - `address: String` - Connection address of this (IMAP/SMTP) service
- **Created** - `created: Instant` - Time when this email service was added to the database
- **Name** - `name: String` - Name of this email service. Empty if not defined.

##### Referenced from
- [Email Service User](#email-service-user).`serviceId`

#### Email Service User
Represents a user of a specific emailing service

##### Details

##### Properties
Email Service User contains the following 4 properties:
- **Service Id** - `serviceId: Int` - Id of the used emailing service
  - Refers to [Email Service](#email-service)
- **Address Id** - `addressId: Int` - Email address that represents this user
  - Refers to [Address](#address)
- **Password** - `password: String` - Password used for authenticating to the email service. Empty if password should be provided externally.
- **Created** - `created: Instant` - Time when this email service user was added to the database

#### Message
Represents a message sent between two or more individuals or entities

##### Details
- **Chronologically** indexed
- Uses 2 database **indices**: `message_id`, `created`

##### Properties
Message contains the following 5 properties:
- **Thread Id** - `threadId: Int` - Id of the thread to which this message belongs
  - Refers to [Message Thread](#message-thread)
- **Sender Id** - `senderId: Int` - Id of the address from which this message was sent
  - Refers to [Address](#address)
- **Message Id** - `messageId: String` - (Unique) id given to this message by the sender
- **Reply To Id** - `replyToId: Option[Int]` - Id of the message this message replies to, if applicable
  - Refers to [Message](#message)
- **Created** - `created: Instant` - Time when this message was sent

##### Referenced from
- [Attachment Message Link](#attachment-message-link).`messageId`
- [Message](#message).`replyToId`
- [Message Recipient Link](#message-recipient-link).`messageId`
- [Message Statement Link](#message-statement-link).`messageId`
- [Pending Reply Reference](#pending-reply-reference).`messageId`

#### Message Recipient Link
Links a message to it's assigned recipients

##### Details

##### Properties
Message Recipient Link contains the following 3 properties:
- **Message Id** - `messageId: Int` - Id of the sent message
  - Refers to [Message](#message)
- **Recipient Id** - `recipientId: Int` - Id of the message recipient (address)
  - Refers to [Address](#address)
- **Role** - `role: RecipientType` - Role / type of the message recipient

#### Message Statement Link
Documents a statement made within a message

##### Details

##### Properties
Message Statement Link contains the following 3 properties:
- **Message Id** - `messageId: Int` - Id of the message where the statement was made
  - Refers to [Message](#message)
- **Statement Id** - `statementId: Int` - Id of the placed statement
  - Refers to *statement* from another module
- **Order Index** - `orderIndex: Int`, `0` by default - 0-based index that indicates the specific location of the placed text

#### Message Thread
Represents a subject or a header given to a sequence of messages

##### Details
- **Chronologically** indexed
- Uses **index**: `created`

##### Properties
Message Thread contains the following 1 properties:
- **Created** - `created: Instant` - Time when this thread was opened

##### Referenced from
- [Message](#message).`threadId`
- [Message Thread Subject Link](#message-thread-subject-link).`threadId`
- [Pending Thread Reference](#pending-thread-reference).`threadId`

#### Message Thread Subject Link
Connects a subject with a message thread in which it was used

##### Details
- **Chronologically** indexed
- Uses **index**: `created`

##### Properties
Message Thread Subject Link contains the following 3 properties:
- **Thread Id** - `threadId: Int` - Id of the thread where the referenced subject was used
  - Refers to [Message Thread](#message-thread)
- **Subject Id** - `subjectId: Int` - Id of the subject used in the specified thread
  - Refers to [Subject](#subject)
- **Created** - `created: Instant` - Time when this subject was first used in the specified thread

#### Pending Reply Reference
Documents an unresolved reference made from a reply message

##### Details

##### Properties
Pending Reply Reference contains the following 3 properties:
- **Message Id** - `messageId: Int` - Id of the message from which this reference is made from
  - Refers to [Message](#message)
- **Referenced Message Id** - `referencedMessageId: String` - Message id of the referenced message
- **Created** - `created: Instant` - Time when this pending reply reference was added to the database

#### Pending Thread Reference
Used for documenting those message ids involved within threads, that have not been linked to any read message

##### Details

##### Properties
Pending Thread Reference contains the following 3 properties:
- **Thread Id** - `threadId: Int` - Id of the message thread with which the referenced message is linked to
  - Refers to [Message Thread](#message-thread)
- **Referenced Message Id** - `referencedMessageId: String` - Message id belonging to some unread message in the linked thread
- **Created** - `created: Instant` - Time when this pending thread reference was added to the database

#### Subject
Represents a named subject on a message (thread)

##### Details
- Combines with [Message Thread Subject Link](#message-thread-subject-link), creating a **Thread Subject**
- **Chronologically** indexed
- Uses **index**: `created`

##### Properties
Subject contains the following 1 properties:
- **Created** - `created: Instant` - Time when this subject was first used

##### Referenced from
- [Message Thread Subject Link](#message-thread-subject-link).`subjectId`
- [Subject Statement Link](#subject-statement-link).`subjectId`

#### Subject Statement Link
Connects a message thread subject to the statements made within that subject

##### Details

##### Properties
Subject Statement Link contains the following 3 properties:
- **Subject Id** - `subjectId: Int` - Id of the described subject
  - Refers to [Subject](#subject)
- **Statement Id** - `statementId: Int` - Id of the placed statement
  - Refers to *statement* from another module
- **Order Index** - `orderIndex: Int`, `0` by default - 0-based index that indicates the specific location of the placed text

### Text
This package contains the following 2 classes: [Statement Placement](#statement-placement), [Text Placement](#text-placement)

#### Statement Placement
Places a statement within some text

##### Details

##### Properties
Statement Placement contains the following 3 properties:
- **Parent Id** - `parentId: Int` - Id of the text where the placed text appears
- **Statement Id** - `statementId: Int` - Id of the placed statement
  - Refers to *statement* from another module
- **Order Index** - `orderIndex: Int`, `0` by default - 0-based index that indicates the specific location of the placed text

#### Text Placement
Places some type of text to some location within another text

##### Details

##### Properties
Text Placement contains the following 3 properties:
- **Parent Id** - `parentId: Int` - Id of the text where the placed text appears
- **Placed Id** - `placedId: Int` - Id of the text that is placed within the parent text
- **Order Index** - `orderIndex: Int`, `0` by default - 0-based index that indicates the specific location of the placed text
