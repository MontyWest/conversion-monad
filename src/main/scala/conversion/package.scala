import model._
import kafka._

package object conversion {

	type ??[D, A] = Tuple2[D, A]

	def eventToModel(
    event: Event
  ): ??[List[Discrepancy], Model] = {

		def filterMissingCustomerId(
			ownership: List[MeterOwner]
		): ??[List[Discrepancy], List[MeterOwnerModel]]

		def removeDuplicateFroms(
			ownership: List[MeterOwnerModel]
		): ??[List[Discrepancy], List[MeterOwnerModel]]

		def validateNonEmpty(
			ownership: List[MeterOwnerModel]
		): Option[Discrepancy]

		???
  }

}