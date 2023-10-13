# Emissary
Version: **v0.1**  
Updated: 2023-10-13

## Table of Contents
- [Packages & Classes](#packages-and-classes)
  - [Messaging](#messaging)
    - [Address](#address)
    - [Attachment](#attachment)
    - [Message](#message)
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

## Packages and Classes
Below are listed all classes introduced in Emissary, grouped by package and in alphabetical order.  
There are a total number of 2 packages and 12 classes

### Messaging
This package contains the following 8 classes: [Address](#address), [Attachment](#attachment), [Message](#message), [Message Statement Link](#message-statement-link), [Message Thread](#message-thread), [Message Thread Subject Link](#message-thread-subject-link), [Subject](#subject), [Subject Statement Link](#subject-statement-link)

#### Address
Represents an address that represents person or another entity that reads or writes messages.

##### Details
- Uses 2 database **indices**: `address`, `name`

##### Properties
Address contains the following 3 properties:
- **Address** - `address: String`
- **Name** - `name: String` - Human-readable name of this entity, if available
- **Created** - `created: Instant` - Time when this address was added to the database

##### Referenced from
- [Message](#message).`senderId`
- [Message Thread](#message-thread).`authorId`
- [Subject](#subject).`authorId`

#### Attachment
Represents an attached file within a message

##### Details

##### Properties
Attachment contains the following 3 properties:
- **Message Id** - `messageId: Int` - Id of the message to which this file is attached
  - Refers to [Message](#message)
- **Original File Name** - `originalFileName: String` - Name of the attached file, as it was originally sent
- **Stored File Name** - `storedFileName: String` - Name of the attached file, as it appears on the local file system. Empty if identical to the original file name.

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
- [Message Statement Link](#message-statement-link).`messageId`

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
Message Thread contains the following 2 properties:
- **Author Id** - `authorId: Int` - Id of the address / entity that originated this thread
  - Refers to [Address](#address)
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
Subject contains the following 2 properties:
- **Author Id** - `authorId: Int` - Id of the address / entity that first used this subject
  - Refers to [Address](#address)
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
