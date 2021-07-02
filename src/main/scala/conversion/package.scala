import model._
import kafka._

package object conversion {

	type ??[D, A] = Tuple2[D, A]

	def eventToModel(
    event: Event
  ): Model = ???

}