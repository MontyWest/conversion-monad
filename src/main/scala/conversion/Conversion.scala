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

object Conversion {

	def tell[D](discrepancies: List[D]): Conversion[D, Unit] =
    Conversion(discrepancies, ())

  def value[D, A](x: A): Conversion[D, A] = 
  	Conversion(Nil, x)

}