package vf.emissary.test

import utopia.flow.parse.string.Regex
import utopia.flow.util.StringExtensions._

/**
 * Tests attachment path processing
 * @author Mikko Hilpinen
 * @since 17.10.2023, v0.1
 */
object PathProcessingTest extends App
{
	private val dashRegex = Regex.escape('-')
	// private val invalidFileCharacterRegex = !(Regex.letterOrDigit || dashRegex).withinParenthesis
	private val multiDashRegex = dashRegex + dashRegex.oneOrMoreTimes
	
	private val validCharRegex = (Regex.letterOrDigit || dashRegex).withinParenthesis
	private val validTextRegex = validCharRegex.oneOrMoreTimes
	
	assert(validCharRegex("t"))
	assert(!validCharRegex(":"))
	
	assert(validTextRegex("test-string"))
	assert(!validTextRegex("test_string"))
	
	private def processName2(name: String) = validTextRegex.matchesIteratorFrom(name)
		.filter { _.nonEmpty }.mkString("-")
		.replaceEachMatchOf(multiDashRegex, "-")
		.notStartingWith("-").notEndingWith("-")
	
	assert(processName2("test") == "test", processName2("test"))
	assert(processName2("test-string") == "test-string")
	assert(processName2("koira.kekkonen@something_or_other") == "koira-kekkonen-something-or-other")
	assert(processName2("___something__g") == "something-g")
	
	/*
	private def processFileName(fileName: String) = fileName.trim
		.replaceEachMatchOf(invalidFileCharacterRegex, "-")
		.replaceEachMatchOf(multiDashRegex, "-")
		.notStartingWith("-").notEndingWith("-")
	
	println(invalidFileCharacterRegex)
	
	assert(invalidFileCharacterRegex(":"))
	assert(!invalidFileCharacterRegex("t"))
	
	assert(processFileName("test") == "test", processFileName("test"))
	assert(processFileName("test-string") == "test-string")
	assert(processFileName("koira.kekkonen@something_or_other") == "koira-kekkonen-something-or-other")
	assert(processFileName("___something__g") == "something-g")
	*/
	println("Success!")
}
