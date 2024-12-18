package vf.emissary.database.storable.text

import utopia.logos.database.storable.text.TextPlacementDbModelFactoryLike
import utopia.vault.model.immutable.Storable
import vf.emissary.model.factory.text.StatementPlacementFactory

/**
  * Common trait for factories used for constructing statement placement database models
  * @tparam DbModel Type of database interaction models constructed
  * @tparam A Type of read instances
  * @tparam Data Supported data-part type
  * @author Mikko Hilpinen
  * @since 17.12.2024, v1.1
  */
trait StatementPlacementDbModelFactoryLike[+DbModel <: Storable, +A, -Data] 
	extends TextPlacementDbModelFactoryLike[DbModel, A, Data] with StatementPlacementFactory[DbModel]

