package vf.emissary.model.combined.url

import utopia.flow.view.template.Extender
import vf.emissary.model.partial.url.LinkPlacementData
import vf.emissary.model.stored.url.LinkPlacement
import vf.emissary.model.template.Placed

/**
 * Attaches detailed link information to a link-placement entry
 * @author Mikko Hilpinen
 * @since 17.10.2023, v0.1
 */
case class DetailedLinkPlacement(placement: LinkPlacement, link: DetailedLink)
	extends Extender[LinkPlacementData] with Placed
{
	// COMPUTED ------------------------
	
	/**
	 * @return Id of this link placement
	 */
	def id = placement.id
	
	
	// IMPLEMENTED  --------------------
	
	override def wrapped: LinkPlacementData = placement.data
	
	override def orderIndex: Int = wrapped.orderIndex
	
	override def toString = link.toString
}
