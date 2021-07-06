import cats.data.Writer
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
		): Writer[List[Discrepancy], List[MeterOwnerModel]] =
			ownership.traverse { 
				case MeterOwner(Some(customerId), from, to) =>
					Writer.value[List[Discrepancy], Option[MeterOwnerModel]](
						Option(MeterOwnerModel(customerId, from, to))
					)
				case MeterOwner(None, from, _) =>
					Writer.apply[List[Discrepancy], Option[MeterOwnerModel]](
						List(OwnershipRemovedMissingCustomerId(from)),
						Option.empty[MeterOwnerModel]
					) 
			}.map(_.flatten)

		def removeDuplicateFroms(
			ownership: List[MeterOwnerModel]
		): Writer[List[Discrepancy], List[MeterOwnerModel]] =
			ownership
        .groupBy(_.from)
        .toList
        .traverse {
          case (_, hd +: tl) if tl.nonEmpty => 
            Writer.apply[List[Discrepancy], MeterOwnerModel](
            	List(OwnershipsRemovedDuplicatedFrom(tl)),
            	hd
            )
          case (_, ls) =>
						Writer.value[List[Discrepancy], MeterOwnerModel](ls.head)
        }

		def validateNonEmpty(
			ownership: List[MeterOwnerModel]
		): Writer[List[Discrepancy], Unit] =
			Writer.tell[List[Discrepancy]](
				List(MissingOwnershipHistory)
			).whenA(ownership.isEmpty)

		val writer = for {
			converted <- filterMissingCustomerId(event.ownership)
			filtered  <- removeDuplicateFroms(converted)
			_         <- validateNonEmpty(filtered)
		} yield Model(
			event.meterId,
			filtered
		)

		writer.run match {
			case (discrepancies, model) => Conversion(discrepancies, model)
		}
  }

}