package conversion

import cats.Monad

import scala.annotation.tailrec

final case class Conversion[D, A](
	discrepancies: List[D],
	value: A
)

object Conversion {

	def tell[D](discrepancies: List[D]): Conversion[D, Unit] = 
		Conversion(discrepancies, ())

  def value[D, A](x: A): Conversion[D, A] = 
  	Conversion(Nil, x)

  implicit def monadConversion[D]: Monad[Conversion[D, *]] =
    new Monad[Conversion[D, *]] {
	  	override def pure[A](x: A): Conversion[D, A] =
	  		Conversion.value(x)

	    override def flatMap[A, B](fa: Conversion[D, A])(
	    	f: A => Conversion[D, B]
	    ): Conversion[D, B] = {
	    	val con = f(fa.value)
	    	Conversion(fa.discrepancies ++ con.discrepancies, con.value)
			}

			override def tailRecM[A, B](a: A)(
				f: A => Conversion[D, Either[A, B]]
			): Conversion[D, B] = {

	      @tailrec
	      def tailRec(a: Conversion[D, A])(
	        f: Conversion[D, A] => Either[Conversion[D, A], Conversion[D, B]]
	      ): Conversion[D, B] =
	        f(a) match {
	          case Left(a1) => tailRec(a1)(f)
	          case Right(b) => b
	        }

	      tailRec(Conversion.value(a)){ conA =>
	        f(conA.value) match {
	          case Conversion(discrepancies, Left(a)) =>
	            Left(Conversion(conA.discrepancies ++ discrepancies, a))
	          case Conversion(discrepancies, Right(b)) =>
	            Right(Conversion(conA.discrepancies ++ discrepancies, b))
	        }
	      }
	    }
	}
}