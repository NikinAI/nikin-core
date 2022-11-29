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
        val sparkResult = Encoders.product[Test].schema.toDDL
        val ourResult   = Interpreter.mapToDDL[Test](DeriveSchema.gen[Test])
        assertTrue(ourResult == sparkResult)
      },
      test("should correctly generate DDL for case class with optionalFields") {
        val sparkResult = Encoders.product[TestOpt].schema.toDDL
        val ourResult   = Interpreter.mapToDDL[TestOpt](DeriveSchema.gen[TestOpt])
        assertTrue(ourResult == sparkResult)
      },
      test("should correctly generate DDL for case class with tuple") {
        val sparkResult = Encoders.product[TestTuple].schema.toDDL
        val ourResult   = Interpreter.mapToDDL[TestTuple](DeriveSchema.gen[TestTuple])
        assertTrue(ourResult == sparkResult)
      },
      test("should correctly generate DDL for case class with complex maps") {
        val sparkResult     = Encoders.product[TestMap].schema.toDDL
        implicit val schema = DeriveSchema.gen[Test]
        val ourResult       = Interpreter.mapToDDL[TestMap](DeriveSchema.gen[TestMap])
        assertTrue(ourResult == sparkResult)
      },
      test("should correctly generate DDL for case class with all primitive") {
        val sparkResult = Encoders.product[TestPrimitive].schema.toDDL
        val ourResult   = Interpreter.mapToDDL[TestPrimitive](DeriveSchema.gen[TestPrimitive])
        assertTrue(ourResult == sparkResult)
      },
      test("should correctly generate DDL for case class with binary") {
        val sparkResult     = Encoders.product[TestBinary].schema.toDDL
        implicit val schema = DeriveSchema.gen[TestBinary]
        val ourResult       = Interpreter.mapToDDL[TestBinary](DeriveSchema.gen[TestBinary])
        assertTrue(ourResult == sparkResult)
      },
      test("should correctly generate DDL for all array collections") {
        val sparkResult     = Encoders.product[TestArrays].schema.toDDL
        implicit val schema = DeriveSchema.gen[TestArrays]
        val ourResult       = Interpreter.mapToDDL[TestArrays](DeriveSchema.gen[TestArrays])
        assertTrue(ourResult == sparkResult)
      }
    )
}
