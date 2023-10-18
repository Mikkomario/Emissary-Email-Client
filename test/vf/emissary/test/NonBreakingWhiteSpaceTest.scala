package vf.emissary.test

import utopia.flow.parse.string.Regex

import java.nio.charset.StandardCharsets
import scala.util.Try

/**
 * Tests non-breaking white-space removal
 * @author Mikko Hilpinen
 * @since 15.10.2023, v0.1
 */
object NonBreakingWhiteSpaceTest extends App
{
	val nbsp = " "
	val s = "Price: 35 EUR"
	
	val wordSplitRegex = Regex.whiteSpace || Regex.escape(' ') || Regex.newLine
	
	println(s.replace(' ', 'Q'))
	println(wordSplitRegex.split(s).mkString(";"))
	
	val invalidRegex = Regex("[^\\x00-\\x7F]")
	val testStr = "This is a string with non-Unicode character: "
	
	assert(invalidRegex.existsIn(testStr))
	assert(invalidRegex.findFirstFrom(testStr).get == "")
	// assert(!invalidRegex("ä"))
	
	// Character.UnicodeBlock.of('�') == Character.UnicodeBlock.BASIC_LATIN;
	def typeOf(char: Char) = Character.UnicodeBlock.of(char)
	println(typeOf('A'))
	println(typeOf('Ä'))
	println(typeOf('-'))
	println(typeOf(''))
	println(typeOf(''))
	
	/*
	def isUtf8(input: String) = {
		Try {
			val bytes = input.getBytes(StandardCharsets.UTF_8)
			val reconstructed = new String(bytes, StandardCharsets.UTF_8)
			input == reconstructed
		}.getOrElse(false)
	}
	
	assert(!isUtf8("\"This string may contain characters outside of the UTF-8 character set: \u0099\""))
	assert(!isUtf8(testStr))
	assert(isUtf8("Test string"))
	assert(isUtf8("Test String with ä and Ö"))
	*/
	/*
	public static boolean isUTF8 (String input) {
		try {
			byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
			String reconstructedString = new String(bytes, StandardCharsets.UTF_8);
			return input.equals(reconstructedString);
		} catch (Exception e) {
			return false;
		}
	}*/
	
	println(s)
}
