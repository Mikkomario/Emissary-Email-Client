package vf.emissary.model.template

import utopia.vault.model.template.Stored

/**
 * Common trait for stored items that define a specific position where they should be placed in a sequence.
 * @author Mikko Hilpinen
 * @since 17.10.2023, v0.1
 * @tparam Data Wrapped data portion
 * @tparam Id Type of used database id
 */
trait StoredPlaced[+Data <: Placed, +Id] extends Stored[Data, Id] with Placed
{
	override def orderIndex: Int = data.orderIndex
}
