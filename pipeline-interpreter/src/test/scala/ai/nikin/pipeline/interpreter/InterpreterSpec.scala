package ai.nikin.pipeline.interpreter

import zio.Scope
import zio.schema.{DeriveSchema, Schema}
import zio.test.{ZIOSpecDefault, _}
import Schemas._

object InterpreterSpec extends ZIOSpecDefault {
  case class Test(a: Int, b: Int)

  object Test {
    implicit val schema: Schema[Test] = DeriveSchema.gen[Test]
  }

  case class TestOpt(a: Option[Int], b: Int)

  object TestOpt {
    implicit val schema: Schema[TestOpt] = DeriveSchema.gen[TestOpt]

  }

  case class TestTuple(
      a: (Int, String, Boolean, BigInt, BigDecimal, String, Int, Boolean, Long, Float)
  )

  object TestTuple {
    implicit val schema: Schema[TestTuple] = DeriveSchema.gen[TestTuple]
  }

  case class TestPrimitive(
      a: Int,
      b: Boolean,
      c: Byte,
      d: Short,
      e: Int,
      f: Long,
      g: Float,
      h: Double,
      i: BigInt,
      k: BigDecimal
  )

  object TestPrimitive {
    implicit val schema: Schema[TestPrimitive] = DeriveSchema.gen[TestPrimitive]
  }

  case class TestMap(a: Map[Test, Int], b: Map[Int, String])

  object TestMap {
    implicit val schema = DeriveSchema.gen[TestMap]
  }

  case class TestBinary(a: Array[Byte])

  object TestBinary {
    implicit val schema = DeriveSchema.gen[TestBinary]
  }

  case class TestArrays(a: Array[Int], b: Seq[Int], c: Set[Int], d: List[Int])

  object TestArrays {
    implicit val schema = DeriveSchema.gen[TestArrays]
  }

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("InterpreterSpec")(
      test("should correctly generate DDL for simple case class") {
        val expectedResult =
          """CREATE TABLE IF NOT EXISTS Test(
                            |    a INT NOT NULL,
                            |    b INT NOT NULL
                            |) USING DELTA
                            |    LOCATION s3a://BUCKET_PLACEHOLDER/Test""".stripMargin
        val ourResult      = DeltaLakeDDLGenerator.generateDDL[Test]("Test")
        assertTrue(ourResult == expectedResult)
      },
      test("should correctly generate DDL for case class with optionalFields") {
        val expectedResult =
          """CREATE TABLE IF NOT EXISTS TestOpt(
                               |    a INT,
                               |    b INT NOT NULL
                               |) USING DELTA
                               |    LOCATION s3a://BUCKET_PLACEHOLDER/TestOpt""".stripMargin
        val ourResult      = DeltaLakeDDLGenerator.generateDDL[TestOpt]("TestOpt")
        assertTrue(ourResult == expectedResult)
      },
      test("should correctly generate DDL for case class with tuple") {
        val expectedResult =
          """CREATE TABLE IF NOT EXISTS TestTuple(
            |    a STRUCT<_1: INT, _2: STRING, _3: BOOLEAN, _4: DECIMAL(38,0), _5: DECIMAL(38,18), _6: STRING, _7: INT, _8: BOOLEAN, _9: BIGINT, _10: FLOAT>
            |) USING DELTA
            |    LOCATION s3a://BUCKET_PLACEHOLDER/TestTuple""".stripMargin
        val ourResult      = DeltaLakeDDLGenerator.generateDDL[TestTuple]("TestTuple")
        assertTrue(ourResult == expectedResult)
      },
      test("should correctly generate DDL for case class with complex maps") {
        val expectedResult =
          """CREATE TABLE IF NOT EXISTS TestMap(
            |    a MAP<STRUCT<a: INT, b: INT>, INT>,
            |    b MAP<INT, STRING>
            |) USING DELTA
            |    LOCATION s3a://BUCKET_PLACEHOLDER/TestMap""".stripMargin

        val ourResult = DeltaLakeDDLGenerator.generateDDL[TestMap]("TestMap")
        assertTrue(ourResult == expectedResult)
      },
      test("should correctly generate DDL for case class with all primitive") {
        val expectedResult =
          """CREATE TABLE IF NOT EXISTS TestPrimitive(
            |    a INT NOT NULL,
            |    b BOOLEAN NOT NULL,
            |    c TINYINT NOT NULL,
            |    d SMALLINT NOT NULL,
            |    e INT NOT NULL,
            |    f BIGINT NOT NULL,
            |    g FLOAT NOT NULL,
            |    h DOUBLE NOT NULL,
            |    i DECIMAL(38,0),
            |    k DECIMAL(38,18)
            |) USING DELTA
            |    LOCATION s3a://BUCKET_PLACEHOLDER/TestPrimitive""".stripMargin

        val ourResult = DeltaLakeDDLGenerator.generateDDL[TestPrimitive]("TestPrimitive")
        assertTrue(ourResult == expectedResult)
      },
      test("should correctly generate DDL for case class with binary") {
        val expectedResult =
          """CREATE TABLE IF NOT EXISTS TestBinary(
            |    a BINARY
            |) USING DELTA
            |    LOCATION s3a://BUCKET_PLACEHOLDER/TestBinary""".stripMargin

        val ourResult = DeltaLakeDDLGenerator.generateDDL[TestBinary]("TestBinary")
        assertTrue(ourResult == expectedResult)
      },
      test("should correctly generate DDL for all array collections") {
        val expectedResult =
          """CREATE TABLE IF NOT EXISTS TestArrays(
            |    a ARRAY<INT>,
            |    b ARRAY<INT>,
            |    c ARRAY<INT>,
            |    d ARRAY<INT>
            |) USING DELTA
            |    LOCATION s3a://BUCKET_PLACEHOLDER/TestArrays""".stripMargin

        val ourResult = DeltaLakeDDLGenerator.generateDDL[TestArrays]("TestArrays")

        assertTrue(ourResult == expectedResult)
      }
    )
}
