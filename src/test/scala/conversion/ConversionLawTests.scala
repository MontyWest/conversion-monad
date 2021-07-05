package conversion

import cats.Eq
import cats.implicits._
import cats.laws.discipline.MonadTests
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.prop.Configuration
import org.typelevel.discipline.scalatest.FunSuiteDiscipline


class ConversionLawTests 
  extends AnyFunSuite 
  with FunSuiteDiscipline 
  with Configuration {

  implicit def arbConversion[
    D: Arbitrary,
    A: Arbitrary
  ]: Arbitrary[Conversion[D, A]] =
    Arbitrary(
      for {
        discrepancies <- Gen.listOf(Arbitrary.arbitrary[D])
        value <- Arbitrary.arbitrary[A]
      } yield Conversion.apply(discrepancies, value)
    )

  implicit def eqConversion[D: Eq, A: Eq]: Eq[Conversion[D, A]] = 
    Eq.by { conversion => (conversion.discrepancies, conversion.value) }

  checkAll(
    "Conversion.MonadLaws", 
    MonadTests[Conversion[String, *]].monad[Int, String, Boolean]
  )
}
