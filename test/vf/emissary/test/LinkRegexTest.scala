package vf.emissary.test

import vf.emissary.model.stored.url.{Domain, Link}

/**
 * Tests regular expressions for finding links
 * @author Mikko Hilpinen
 * @since 16.10.2023, v0.1
 */
object LinkRegexTest extends App
{
	val domain1 = "https://api.service.com/"
	val domain2 = "http://128.0.0.1:8080/"
	val domain3 = "www.google.com"
	
	val url1 = s"${domain1}v1/examples?type=resource"
	val url2 = s"${domain2}service"
	
	assert(Domain.regex(domain1))
	assert(Domain.regex(domain2))
	assert(Domain.regex(domain3))
	
	assert(Domain.regex.findFirstFrom(url1).get == domain1)
	assert(Domain.regex.findFirstFrom(url2).get == domain2)
	assert(Domain.regex.findFirstFrom(domain3).get == domain3)
	
	assert(Link.regex(url1))
	assert(Link.regex(url2))
	assert(Link.regex(domain3))
	
	assert(Link.paramPartRegex.findFirstFrom(url1).get == "?type=resource")
	assert(!Link.paramPartRegex.existsIn(url2))
	
	println("Success!")
}
