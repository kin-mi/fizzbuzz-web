package backend

import org.scalatest._
import org.scalatest.matchers._

abstract class UnitSpec
    extends flatspec.AnyFlatSpec
    with should.Matchers
    with BeforeAndAfterEach
