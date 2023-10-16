# Emissary
Version: **v0.1**  
Updated: 2023-10-16

## Table of Contents
- [Enumerations](#enumerations)
  - [Recipient Type](#recipient-type)
- [Packages & Classes](#packages-and-classes)
  - [Messaging](#messaging)
    - [Address](#address)
    - [Address Name](#address-name)
    - [Attachment](#attachment)
    - [Message](#message)
    - [Message Recipient Link](#message-recipient-link)
    - [Message Statement Link](#message-statement-link)
    - [Message Thread](#message-thread)
    - [Message Thread Subject Link](#message-thread-subject-link)
    - [Subject](#subject)
    - [Subject Statement Link](#subject-statement-link)
  - [Text](#text)
    - [Delimiter](#delimiter)
    - [Statement](#statement)
    - [Word](#word)
    - [Word Placement](#word-placement)
  - [Url](#url)
    - [Domain](#domain)
    - [Link](#link)
    - [Link Placement](#link-placement)
    - [Request Path](#request-path)

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
There are a total number of 3 packages and 18 classes

### Messaging
This package contains the following 10 classes: [Address](#address), [Address Name](#address-name), [Attachment](#attachment), [Message](#message), [Message Recipient Link](#message-recipient-link), [Message Statement Link](#message-statement-link), [Message Thread](#message-thread), [Message Thread Subject Link](#message-thread-subject-link), [Subject](#subject), [Subject Statement Link](#subject-statement-link)

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

##### Properties
Attachment contains the following 2 properties:
- **Message Id** - `messageId: Int` - Id of the message to which this file is attached
  - Refers to [Message](#message)
- **File Name** - `fileName: String` - Name of the attached file, as appears on the file system

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
- [Attachment](#attachment).`messageId`
- [Message](#message).`replyToId`
- [Message Recipient Link](#message-recipient-link).`messageId`
- [Message Statement Link](#message-statement-link).`messageId`

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
- Uses **index**: `order_index`

##### Properties
Message Statement Link contains the following 3 properties:
- **Message Id** - `messageId: Int` - Id of the message where the statement was made
  - Refers to [Message](#message)
- **Statement Id** - `statementId: Int` - The statement that was made
  - Refers to [Statement](#statement)
- **Order Index** - `orderIndex: Int` - Index of the statement in the message (0-based)

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

#### Subject
Represents a named subject on a message (thread)

##### Details

##### Properties
Subject contains the following 1 properties:
- **Created** - `created: Instant` - Time when this subject was first used

##### Referenced from
- [Message Thread Subject Link](#message-thread-subject-link).`subjectId`
- [Subject Statement Link](#subject-statement-link).`subjectId`

#### Subject Statement Link
Connects a message thread subject to the statements made within that subject

##### Details
- Uses **index**: `order_index`

##### Properties
Subject Statement Link contains the following 3 properties:
- **Subject Id** - `subjectId: Int` - Id of the described subject
  - Refers to [Subject](#subject)
- **Statement Id** - `statementId: Int` - Id of the statement made within the referenced subject
  - Refers to [Statement](#statement)
- **Order Index** - `orderIndex: Int` - Index where this statement appears within the referenced subject (0-based)

### Text
This package contains the following 4 classes: [Delimiter](#delimiter), [Statement](#statement), [Word](#word), [Word Placement](#word-placement)

#### Delimiter
Represents a character sequence used to separate two statements or parts of a statement

##### Details
- Uses **index**: `text`

##### Properties
Delimiter contains the following 2 properties:
- **Text** - `text: String` - The characters that form this delimiter
- **Created** - `created: Instant` - Time when this delimiter was added to the database

##### Referenced from
- [Statement](#statement).`delimiterId`

#### Statement
Represents an individual statement made within some text. Consecutive statements form whole texts.

##### Details
- Combines with [Message Statement Link](#message-statement-link), creating a **Message Statement**
- Combines with [Subject Statement Link](#subject-statement-link), creating a **Subject Statement**
- **Chronologically** indexed
- Uses **index**: `created`

##### Properties
Statement contains the following 2 properties:
- **Delimiter Id** - `delimiterId: Option[Int]` - Id of the delimiter that terminates this sentence. None if this sentence is not terminated with any character.
  - Refers to [Delimiter](#delimiter)
- **Created** - `created: Instant` - Time when this statement was first made

##### Referenced from
- [Link Placement](#link-placement).`statementId`
- [Message Statement Link](#message-statement-link).`statementId`
- [Subject Statement Link](#subject-statement-link).`statementId`
- [Word Placement](#word-placement).`statementId`

#### Word
Represents an individual word used in a text document. Case-sensitive.

##### Details
- Combines with [Word Placement](#word-placement), creating a **Stated Word**
- Uses **index**: `text`

##### Properties
Word contains the following 2 properties:
- **Text** - `text: String` - Text representation of this word
- **Created** - `created: Instant` - Time when this word was added to the database

##### Referenced from
- [Word Placement](#word-placement).`wordId`

#### Word Placement
Records when a word is used in a statement

##### Details
- Uses **index**: `order_index`

##### Properties
Word Placement contains the following 3 properties:
- **Statement Id** - `statementId: Int` - Id of the statement where the referenced word appears
  - Refers to [Statement](#statement)
- **Word Id** - `wordId: Int` - Id of the word that appears in the described statement
  - Refers to [Word](#word)
- **Order Index** - `orderIndex: Int` - Index at which the specified word appears within the referenced statement (0-based)

### Url
This package contains the following 4 classes: [Domain](#domain), [Link](#link), [Link Placement](#link-placement), [Request Path](#request-path)

#### Domain
Represents the address of an internet service

##### Details

##### Properties
Domain contains the following 2 properties:
- **Url** - `url: String` - Full http(s) address of this domain in string format. Includes protocol, domain name and possible port number.
- **Created** - `created: Instant` - Time when this domain was added to the database

##### Referenced from
- [Request Path](#request-path).`domainId`

#### Link
Represents a link for a specific http(s) request

##### Details

##### Properties
Link contains the following 3 properties:
- **Request Path Id** - `requestPathId: Int` - Id of the targeted internet address, including the specific sub-path
  - Refers to [Request Path](#request-path)
- **Query Parameters** - `queryParameters: Model` - Specified request parameters in model format
- **Created** - `created: Instant` - Time when this link was added to the database

##### Referenced from
- [Link Placement](#link-placement).`linkId`

#### Link Placement
Places a link within a statement

##### Details

##### Properties
Link Placement contains the following 3 properties:
- **Statement Id** - `statementId: Int` - Id of the statement where the specified link is referenced
  - Refers to [Statement](#statement)
- **Link Id** - `linkId: Int` - Referenced link
  - Refers to [Link](#link)
- **Order Index** - `orderIndex: Int` - Index where the link appears in the statement (0-based)

#### Request Path
Represents a specific http(s) request url, not including any query parameters

##### Details
- Combines with [Domain](#domain), creating a **Detailed Request Path**

##### Properties
Request Path contains the following 3 properties:
- **Domain Id** - `domainId: Int` - Id of the domain part of this url
  - Refers to [Domain](#domain)
- **Path** - `path: String` - Part of this url that comes after the domain part. Doesn't include any query parameters, nor the initial forward slash.
- **Created** - `created: Instant` - Time when this request path was added to the database

##### Referenced from
- [Link](#link).`requestPathId`
