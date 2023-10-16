package vf.emissary.database.access.many.url.request_path

import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.sql.Condition
import vf.emissary.database.factory.url.RequestPathFactory
import vf.emissary.model.stored.url.RequestPath

object ManyRequestPathsAccess
{
	// NESTED	--------------------
	
	private class ManyRequestPathsSubView(condition: Condition) extends ManyRequestPathsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def globalCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple request paths at a time
  * @author Mikko Hilpinen
  * @since 16.10.2023, v0.1
  */
trait ManyRequestPathsAccess 
	extends ManyRequestPathsAccessLike[RequestPath, ManyRequestPathsAccess] 
		with ManyRowModelAccess[RequestPath]
{
	// IMPLEMENTED	--------------------
	
	override def factory = RequestPathFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyRequestPathsAccess = 
		new ManyRequestPathsAccess.ManyRequestPathsSubView(mergeCondition(filterCondition))
}

