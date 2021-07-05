import model._
import kafka._

package object conversion {

	def eventToModel(
    event: Event
  ): Conversion[Discrepancy, Model] = {

		def filterMissingCustomerId(
			ownership: List[MeterOwner]
		): Conversion[Discrepancy, List[MeterOwnerModel]]

		def removeDuplicateFroms(
			ownership: List[MeterOwnerModel]
		): Conversion[Discrepancy, List[MeterOwnerModel]]

		def validateNonEmpty(
			ownership: List[MeterOwnerModel]
		): Conversion[Discrepancy, Unit]

		???
  }

}