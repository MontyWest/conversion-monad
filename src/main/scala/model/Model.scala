package model

import java.time.Instant

case class Model(
	meterId: String,
	ownership: List[MeterOwnerModel]
)

case class MeterOwnerModel(
	customerId: String,
	from: Instant,
	to: Option[Instant]
)