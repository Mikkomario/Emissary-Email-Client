package vf.emissary.database.factory.text

import utopia.logos.database.factory.text.TextPlacementDbFactoryLike
import vf.emissary.database.props.text.StatementPlacementDbProps

/**
  * Common trait for factories which parse statement placement data from database-originated models
  * @tparam A Type of read instances
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait StatementPlacementDbFactoryLike[+A] extends TextPlacementDbFactoryLike[A]
{
	// ABSTRACT	--------------------
	
	/**
	  * Database properties used when parsing column data
	  */
	override def dbProps: StatementPlacementDbProps
}

