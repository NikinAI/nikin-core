package ai.nikin.pipeline.interpreter

import org.apache.spark.sql.Encoders
import zio.Scope
import zio.schema.DeriveSchema
import zio.test.{ZIOSpecDefault, _}
import Schemas._

object InterpreterSpec extends ZIOSpecDefault {
  case class Test(a: Int, b: Int)
  case class TestOpt(a: Option[Int], b: Int)

  case class TestTuple(
      a: (Int, String, Boolean, BigInt, BigDecimal, String, Int, Boolean, Long, Float)
  )

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

  case class TestMap(a: Map[Test, Int], b: Map[Int, String])
  case class TestBinary(a: Array[Byte])
  case class TestArrays(a: Array[Int], b: Seq[Int], c: Set[Int], d: List[Int])

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("InterpreterSpec")(
      test("should correctly generate DDL for simple case class") {
        val expectedResult =
          """CREATE TABLE IF NOT EXISTS Test(
                            |    a INT NOT NULL,
                            |    b INT NOT NULL
                            |) USING DELTA
                            |    LOCATION s3a://BUCKET_PLACEHOLDER/Test""".stripMargin
        val ourResult      = DeltaLakeKeeperInterpreter.generateDDL[Test](DeriveSchema.gen[Test])
        assertTrue(ourResult == expectedResult)
      },
      test("should correctly generate DDL for case class with optionalFields") {
        val expectedResult =
          """CREATE TABLE IF NOT EXISTS TestOpt(
                               |    a INT,
                               |    b INT NOT NULL
                               |) USING DELTA
                               |    LOCATION s3a://BUCKET_PLACEHOLDER/TestOpt""".stripMargin
        val ourResult      = DeltaLakeKeeperInterpreter.generateDDL[TestOpt](DeriveSchema.gen[TestOpt])
        assertTrue(ourResult == expectedResult)
      },
      test("should correctly generate DDL for case class with tuple") {
        val expectedResult =
          """CREATE TABLE IF NOT EXISTS TestTuple(
            |    a STRUCT<_1: INT, _2: STRING, _3: BOOLEAN, _4: DECIMAL(38,0), _5: DECIMAL(38,18), _6: STRING, _7: INT, _8: BOOLEAN, _9: BIGINT, _10: FLOAT>
            |) USING DELTA
            |    LOCATION s3a://BUCKET_PLACEHOLDER/TestTuple""".stripMargin
        val ourResult      =
          DeltaLakeKeeperInterpreter.generateDDL[TestTuple](DeriveSchema.gen[TestTuple])
        assertTrue(ourResult == expectedResult)
      },
      test("should correctly generate DDL for case class with complex maps") {
        val expectedResult =
          """CREATE TABLE IF NOT EXISTS TestMap(
            |    a MAP<STRUCT<a: INT, b: INT>, INT>,
            |    b MAP<INT, STRING>
            |) USING DELTA
            |    LOCATION s3a://BUCKET_PLACEHOLDER/TestMap""".stripMargin

        implicit val schema = DeriveSchema.gen[Test]
        val ourResult       = DeltaLakeKeeperInterpreter.generateDDL[TestMap](DeriveSchema.gen[TestMap])
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

        val ourResult =
          DeltaLakeKeeperInterpreter.generateDDL[TestPrimitive](DeriveSchema.gen[TestPrimitive])
        assertTrue(ourResult == expectedResult)
      },
      test("should correctly generate DDL for case class with binary") {
        val expectedResult =
          """CREATE TABLE IF NOT EXISTS TestBinary(
            |    a BINARY
            |) USING DELTA
            |    LOCATION s3a://BUCKET_PLACEHOLDER/TestBinary""".stripMargin

        implicit val schema = DeriveSchema.gen[TestBinary]
        val ourResult       =
          DeltaLakeKeeperInterpreter.generateDDL[TestBinary](DeriveSchema.gen[TestBinary])
        assertTrue(ourResult == expectedResult)
      },
      test("should correctly generate DDL for all array collections") {
        val expectedResult  =
          """CREATE TABLE IF NOT EXISTS TestArrays(
            |    a ARRAY<INT>,
            |    b ARRAY<INT>,
            |    c ARRAY<INT>,
            |    d ARRAY<INT>
            |) USING DELTA
            |    LOCATION s3a://BUCKET_PLACEHOLDER/TestArrays""".stripMargin
        implicit val schema = DeriveSchema.gen[TestArrays]
        val ourResult       =
          DeltaLakeKeeperInterpreter.generateDDL[TestArrays](DeriveSchema.gen[TestArrays])

        assertTrue(ourResult == expectedResult)
      }
    )
}
