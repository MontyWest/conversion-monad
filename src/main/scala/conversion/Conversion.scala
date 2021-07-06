package conversion

final case class Conversion[D, A](
	discrepancies: List[D],
	value: A
)

