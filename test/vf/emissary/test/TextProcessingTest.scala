package vf.emissary.test

import utopia.flow.parse.file.FileExtensions._
import utopia.flow.parse.string.StringFrom
import vf.emissary.controller.archive.ArchiveEmails
import vf.emissary.model.stored.text.Delimiter

import java.nio.file.{Path, Paths}
import scala.io.{Codec, StdIn}

/**
 * Tests text processing
 * @author Mikko Hilpinen
 * @since 18.10.2023, v0.1
 */
object TextProcessingTest extends App
{
	implicit val codec: Codec = Codec.UTF8
	
	// data/test-data/test-input/example-2.txt
	println(s"Please specify a path to the file to process (relative to ${ Paths.get("").absolute })")
	val path: Path = StdIn.readLine()
	val str = StringFrom.path(path).get
	
	println("Raw input:")
	println(str)
	
	println("\n----------------------\n")
	
	println("Processed text:")
	val processed = ArchiveEmails.processText(str, skipReplyLines = true)
	println(processed)
	
	println("\n----------------------\n")
	
	val delimiterSplit = Delimiter.anyDelimiterRegex.divide(processed)
	delimiterSplit.foreach {
		case Left(str) => println(str)
		case Right(delimiter) => println(s"Delimiter: \"$delimiter\"")
	}
}
