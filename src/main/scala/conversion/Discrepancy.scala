package conversion

import java.time.Instant
import model._

sealed trait Discrepancy

object Discrepancy {
	case class OwnershipRemovedMissingCustomerId(
		removedFrom: Instant
	) extends Discrepancy

  case class OwnershipsRemovedDuplicatedFrom(
  	removed: List[MeterOwnerModel]
  ) extends Discrepancy

  case object MissingOwnershipHistory
  	extends Discrepancy
}