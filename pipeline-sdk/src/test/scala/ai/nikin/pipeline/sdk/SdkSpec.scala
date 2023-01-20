package ai.nikin.pipeline.sdk

import ai.nikin.pipeline.model.dsl._
import ai.nikin.pipeline.model.dsl.AggregationFunction.{Avg, Sum}
import ai.nikin.pipeline.sdk.schemas.{RecordA, RecordB}

class SdkSpec extends TestUtils {
  test("SDK - aggregation to lake") {
    val pipeline: Transformation[RecordA, RecordB] F_>>> Lake[RecordB] =
      aggregation[RecordA, RecordB]("s1", Sum("col1", "col2")) >>> lake[RecordB]("t1")

    println(pipeline)

    testContained(
      compileErrors(
        """ aggregation[RecordB, RecordA]("s1", Sum("col1", "col2")) >>> lake[RecordB]("t1") """
      ),
      "inferred type arguments",
      "do not conform to method",
      "type parameter bounds"
    )

    testContained(
      compileErrors(
        """ lake[RecordB]("t1") >>> lake[RecordB]("t2") """
      ),
      "To enable this connectivity, add:"
    )
  }

  test("SDK - lake to aggregation to lake") {
    val pipeline =
      lake[RecordA]("lA") >>> aggregation[RecordA, RecordB]("tAB", Avg("col1", "col2")) >>>
        lake[RecordB]("lB")

    println(pipeline.asGraph)
    println(pipeline)
  }

  test("SDK - Schema FQN is captured") {
    assertEquals(lake[RecordA]("lA").tpe, classOf[RecordA].getCanonicalName)

    val agg = aggregation[RecordA, RecordB]("tAB", Avg("col1", "col2"))
    assertEquals(agg.inputTpe, classOf[RecordA].getCanonicalName)
    assertEquals(agg.outputTpe, classOf[RecordB].getCanonicalName)
  }

  test("Untyped DSL Model") {
    import io.scalaland.chimney.dsl._

    val table = lake[RecordA]("lA")
    assertEquals(table.transformInto[UntypedLake], UntypedLake(table.name, table.tpe))

    val agg = aggregation[RecordA, RecordB]("tAB", Avg("col1", "col2")).asInstanceOf[Aggregation[RecordA, RecordB]]
    assertEquals(agg.transformInto[UntypedAggregation], UntypedAggregation(agg.name, agg.aggFunction, agg.inputTpe, agg.outputTpe))
  }
}
