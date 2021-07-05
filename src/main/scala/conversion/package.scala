import model._
import kafka._
import cats.implicits._
import conversion.Discrepancy._


package object conversion {

	def eventToModel(
    event: Event
  ): Conversion[Discrepancy, Model] = {

		def filterMissingCustomerId(
			ownership: List[MeterOwner]
		): Conversion[Discrepancy, List[MeterOwnerModel]] = 
			ownership.traverse { 
				case MeterOwner(Some(customerId), from, to) =>
					Conversion.value[Discrepancy, Option[MeterOwnerModel]](
						Option(MeterOwnerModel(customerId, from, to))
					)
				case MeterOwner(None, from, _) =>
					Conversion.apply[Discrepancy, Option[MeterOwnerModel]](
						List(OwnershipRemovedMissingCustomerId(from)),
						Option.empty[MeterOwnerModel]
					) 
			}.map(_.flatten)

		def removeDuplicateFroms(
			ownership: List[MeterOwnerModel]
		): Conversion[Discrepancy, List[MeterOwnerModel]] =
			ownership
        .groupBy(_.from)
        .toList
        .traverse {
          case (_, hd +: tl) if tl.nonEmpty => 
            Conversion[Discrepancy, MeterOwnerModel](
            	List(OwnershipsRemovedDuplicatedFrom(tl)),
            	hd
            )
          case (_, ls) =>
            Conversion.value[Discrepancy, MeterOwnerModel](ls.head)
        }

		def validateNonEmpty(
			ownership: List[MeterOwnerModel]
		): Conversion[Discrepancy, Unit] = 
			Conversion.tell[Discrepancy](
				List(MissingOwnershipHistory)
			).whenA(ownership.isEmpty)

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