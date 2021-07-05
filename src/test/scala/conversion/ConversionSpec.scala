package conversion

import cats.syntax.option._
import kafka._
import model._
import org.scalatest.Inside
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import java.time.Instant

class ConversionSpec
  extends AnyFlatSpec
  with should.Matchers
  with Inside {

  private val testMeterId = "1234"

  "eventToModel" should "convert with no discrepancies if well formed" in {
    val inputEvent = Event(
      meterId = testMeterId,
      ownership = List(
        MeterOwner(
          customerId = "abc".some,
          from = Instant.parse("2018-02-09T00:00:00Z"),
          to = Instant.parse("2019-12-25T00:00:00Z").some
        ),
        MeterOwner(
          customerId = "xyz".some,
          from = Instant.parse("2019-12-25T00:00:00Z"),
          to = None
        ),
      )
    )

    inside(eventToModel(inputEvent)) {
      case Conversion(discrepancies, model) =>
        discrepancies shouldBe empty
        model shouldBe Model(
          meterId = testMeterId,
          ownership = List(
            MeterOwnerModel(
              customerId = "abc",
              from = Instant.parse("2018-02-09T00:00:00Z"),
              to = Instant.parse("2019-12-25T00:00:00Z").some
            ),
            MeterOwnerModel(
              customerId = "xyz",
              from = Instant.parse("2019-12-25T00:00:00Z"),
              to = None
            )
          )
        )
    }
  }

  it should "add discrepancy and filter any missing accountIds" in {
    val inputEvent = Event(
      meterId = testMeterId,
      ownership = List(
        MeterOwner(
          customerId = "abc".some,
          from = Instant.parse("2018-02-09T00:00:00Z"),
          to = Instant.parse("2019-12-25T00:00:00Z").some
        ),
        MeterOwner(
          customerId = None,
          from = Instant.parse("2019-12-25T00:00:00Z"),
          to = Instant.parse("2020-07-31T00:00:00Z").some
        ),
        MeterOwner(
          customerId = "xyz".some,
          from = Instant.parse("2020-07-31T00:00:00Z"),
          to = None
        )
      )
    )

    inside(eventToModel(inputEvent)) {
      case Conversion(discrepancies, model) =>

        discrepancies should contain theSameElementsAs List(
          Discrepancy.OwnershipRemovedMissingCustomerId(Instant.parse("2019-12-25T00:00:00Z"))
        )

        model shouldBe Model(
          meterId = testMeterId,
          ownership = List(
            MeterOwnerModel(
              customerId = "abc",
              from = Instant.parse("2018-02-09T00:00:00Z"),
              to = Instant.parse("2019-12-25T00:00:00Z").some
            ),
            MeterOwnerModel(
              customerId = "xyz",
              from = Instant.parse("2020-07-31T00:00:00Z"),
              to = None
            )
          )
        )
    }
  }

  it should "add discrepancy and filter duplicated from dates, favouring the shorter ownership time" in {
    val inputEvent = Event(
      meterId = testMeterId,
      ownership = List(
        MeterOwner(
          customerId = "abc".some,
          from = Instant.parse("2018-02-09T00:00:00Z"),
          to = Instant.parse("2019-12-25T00:00:00Z").some
        ),
        MeterOwner(
          customerId = "rst".some,
          from = Instant.parse("2019-12-25T00:00:00Z"),
          to = Instant.parse("2020-07-31T00:00:00Z").some
        ),
        MeterOwner(
          customerId = "xyz".some,
          from = Instant.parse("2019-12-25T00:00:00Z"),
          to = None
        )
      )
    )

    inside(eventToModel(inputEvent)) {
      case Conversion(discrepancies, model) =>

        discrepancies should contain theSameElementsAs List(
          Discrepancy.OwnershipsRemovedDuplicatedFrom(
            List(
              MeterOwnerModel(
                customerId = "xyz",
                from = Instant.parse("2019-12-25T00:00:00Z"),
                to = None
              )
            )
          )
        )

        model shouldBe Model(
          meterId = testMeterId,
          ownership = List(
            MeterOwnerModel(
              customerId = "abc",
              from = Instant.parse("2018-02-09T00:00:00Z"),
              to = Instant.parse("2019-12-25T00:00:00Z").some
            ),
            MeterOwnerModel(
              customerId = "rst",
              from = Instant.parse("2019-12-25T00:00:00Z"),
              to = Instant.parse("2020-07-31T00:00:00Z").some
            )
          )
        )
    }
  }

  it should "add discrepancy if ownership is empty" in {
    val inputEvent = Event(
      meterId = testMeterId,
      ownership = Nil
    )

    inside(eventToModel(inputEvent)) {
      case Conversion(discrepancies, model) =>

        discrepancies should contain theSameElementsAs List(
          Discrepancy.MissingOwnershipHistory
        )

        model shouldBe Model(
          meterId = testMeterId,
          ownership = Nil
        )
    }
  }

  it should "add discrepancy if ownership becomes empty after filtering" in {
    val inputEvent = Event(
      meterId = testMeterId,
      ownership = List(
        MeterOwner(
          customerId = None,
          from = Instant.parse("2019-12-25T00:00:00Z"),
          to = Instant.parse("2020-07-31T00:00:00Z").some
        )
      )
    )

    inside(eventToModel(inputEvent)) {
      case Conversion(discrepancies, model) =>

        discrepancies should contain theSameElementsAs List(
          Discrepancy.OwnershipRemovedMissingCustomerId(Instant.parse("2019-12-25T00:00:00Z")),
          Discrepancy.MissingOwnershipHistory
        )

        model shouldBe Model(
          meterId = testMeterId,
          ownership = Nil
        )
    }
  }
}
