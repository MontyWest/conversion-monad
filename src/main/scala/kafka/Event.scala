package kafka

import java.time.Instant

case class Event(
	meterId: String,
	ownership: List[MeterOwner]
)

case class MeterOwner(
	customerId: Option[String],
	from: Instant,
	to: Option[Instant]
)