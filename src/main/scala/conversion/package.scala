import model._
import kafka._
import cats.implicits._


package object conversion {

	def eventToModel(
    event: Event
  ): Conversion[Discrepancy, Model] = {

		def filterMissingCustomerId(
			ownership: List[MeterOwner]
		): Conversion[Discrepancy, List[MeterOwnerModel]] = ???

		def removeDuplicateFroms(
			ownership: List[MeterOwnerModel]
		): Conversion[Discrepancy, List[MeterOwnerModel]] = ???

		def validateNonEmpty(
			ownership: List[MeterOwnerModel]
		): Conversion[Discrepancy, Unit] = ???

		for {
			converted <- filterMissingCustomerId(event.ownership)
			filtered  <- removeDuplicateFroms(converted)
			_         <- validateNonEmpty(filtered)
		} yield Model(
			event.meterId,
			filtered
		)
  }

}