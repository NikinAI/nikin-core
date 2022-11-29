package ai.nikin.pipeline.sdk.dsl

import ai.nikin.pipeline.sdk.TestUtils.testContained
import ai.nikin.pipeline.sdk.dsl.edge.{lakeToTransform, transformToLake, F_>>>}
import ai.nikin.pipeline.sdk.dsl.model.{RecordA, RecordB}
import ai.nikin.pipeline.sdk.dsl.vertices.Aggregation.{Avg, Sum}
import ai.nikin.pipeline.sdk.dsl.vertices.{aggregation, lake, Aggregation, Lake, Transformation}
import ai.nikin.typedgraph.core._
import munit.FunSuite

class DslSpec extends FunSuite {
  test("DSL - aggregation to lake") {
    val pipeline: Transformation[RecordA, RecordB] F_>>> Lake[RecordB] =
      aggregation[RecordA, RecordB]("s1", Sum("col1")) >>> lake[RecordB]("t1")

    println(pipeline)

    testContained(
      compileErrors(
        """ aggregation[RecordB, RecordA]("s1", Sum("col1")) >>> lake[RecordB]("t1") """
      ),
      "inferred type arguments",
      "do not conform to method",
      "type parameter bounds"
    )

    testContained(
      compileErrors(
        """ lake[RecordB]("t1") >>> lake[RecordB]("t2") """
      ),
      "could not find implicit value for parameter"
    )
  }

  test("DSL - lake to aggregation to lake") {
    val pipeline =
      lake[RecordA]("lA") >>> aggregation[RecordA, RecordB]("tAB", Avg("col1")) >>>
        lake[RecordB]("lB")

    println(pipeline.inGraph.toDot)
    println(pipeline)
  }
}