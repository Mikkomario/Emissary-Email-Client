package vf.emissary.test

import utopia.flow.parse.string.Regex

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
	
	println(s)
}
