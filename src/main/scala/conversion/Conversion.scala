package conversion

/**
 *  Needs to be
 *   - performed sequentially: 
 * 		 - flatMap on the right
 *     - combine on the left
 *   - a List of Conversion can be traversed:
 *     - applicative on the right
 *     - combine on the left
 */
final case class Conversion[D, A](
	discrepancies: List[D],
	converted: A
)