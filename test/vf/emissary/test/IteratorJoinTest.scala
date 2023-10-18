package vf.emissary.test

import utopia.flow.view.immutable.caching.Lazy

/**
 * Tests how iterator join works
 * @author Mikko Hilpinen
 * @since 18.10.2023, v0.1
 */
object IteratorJoinTest extends App
{
	var consumed = false
	val lazyIter2 = Lazy { Iterator.single {
		consumed = true
		2
	} }
	val iter1 = Iterator.single(1)
	
	val iter3 = iter1 ++ lazyIter2.valueIterator.flatten
	
	assert(!consumed)
	assert(lazyIter2.nonInitialized)
	assert(iter1.hasNext)
	
	assert(iter3.next() == 1)
	assert(!consumed)
	assert(lazyIter2.nonInitialized)
	assert(!iter1.hasNext)
	
	assert(iter3.next() == 2)
	assert(consumed)
	assert(lazyIter2.isInitialized)
	
	println("Success!")
}
