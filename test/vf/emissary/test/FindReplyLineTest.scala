package vf.emissary.test

import utopia.flow.parse.file.FileExtensions._
import utopia.flow.parse.string.{Regex, StringFrom}

import java.nio.file.{Path, Paths}
import scala.io.{Codec, StdIn}

/**
 * @author Mikko Hilpinen
 * @since 18.10.2023, v0.1
 */
object FindReplyLineTest extends App
{
	private lazy val zonerReplyLineRegex = Regex.escape('>') + Regex.any
	private lazy val replyHeaderRegex = Regex.letter + Regex.letter.oneOrMoreTimes + Regex.escape(':') +
		Regex.whiteSpace + Regex.any
	private lazy val anyReplyLineRegex = zonerReplyLineRegex.withinParenthesis || replyHeaderRegex.withinParenthesis
	
	def firstReplyLineIndex(text: String) = {
		val lines = text.linesIterator.toVector
		val result = lines.indices.find { i =>
			anyReplyLineRegex(lines(i)) &&
				((i + 1) to (i + 2))
					.forall { i => lines.lift(i).exists { s => s.isEmpty || anyReplyLineRegex.apply(s) } }
		}
		result.foreach { i => println(lines(i)) }
		result
	}
	
	implicit val codec: Codec = Codec.UTF8
	
	// data/test-data/test-input/example-2.txt
	println(s"Please specify a path to the file to process (relative to ${Paths.get("").absolute})")
	val path: Path = StdIn.readLine()
	val str = StringFrom.path(path).get
	
	firstReplyLineIndex(str) match {
		case Some(index) => println(s"Reply line found at index $index")
		case None => println("No reply lines found")
	}
}
