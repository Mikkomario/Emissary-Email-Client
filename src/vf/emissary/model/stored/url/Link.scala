package vf.emissary.model.stored.url

import utopia.flow.parse.string.Regex
import utopia.vault.model.template.StoredModelConvertible
import vf.emissary.model.partial.url.LinkData

@deprecated("Moved to Logos and renamed to StoredLink", "v1.1")
object Link
{
	private lazy val questionMarkRegex = Regex.escape('?')
	private lazy val pathCharacterRegex = (Regex.letterOrDigit || Regex.anyOf("-._~:/#[]@!$&'()*+,;%="))
		.withinParentheses
	private lazy val urlCharacterRegex = (pathCharacterRegex || questionMarkRegex).withinParentheses
	
	/**
	 * A regular expression that matches to the parameters -part of a link
	 */
	lazy val paramPartRegex = (questionMarkRegex + urlCharacterRegex.oneOrMoreTimes).withinParentheses
	
	/**
	 * A regular expression that matches to links
	 */
	lazy val regex = Domain.regex + pathCharacterRegex.anyTimes + paramPartRegex.noneOrOnce
}

/**
  * Represents a link that has already been stored in the database
  * @param id id of this link in the database
  * @param data Wrapped link data
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
@deprecated("Moved to Logos and renamed to StoredLink", "v1.1")
case class Link(id: Int, data: LinkData) extends StoredModelConvertible[LinkData]
