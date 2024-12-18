package vf.emissary.model.template

@deprecated("Moved to Logos", "v1.1")
object Placed
{
	/**
	 * Ordering used for placing these items based on their order index
	 */
	implicit val ordering: Ordering[Placed] = Ordering.by { _.orderIndex }
}

/**
 * Common trait for items that may be placed within ordered sequences
 * @author Mikko Hilpinen
 * @since 17.10.2023, v0.1
 */
@deprecated("Moved to Logos", "v1.1")
trait Placed
{
	/**
	 * @return Index that determines the position of this item (0-based)
	 */
	def orderIndex: Int
}
