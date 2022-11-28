package ai.nikin.pipeline.sdk.dsl

import ai.nikin.pipeline.sdk.TestUtils.testContained
import ai.nikin.pipeline.sdk.dsl.edge.{lakeToTransform, transformToLake, F_>>>}
import ai.nikin.pipeline.sdk.dsl.model.{RecordA, RecordB}
import ai.nikin.pipeline.sdk.dsl.vertices.{lake, transformation, Lake, Transformation}
import ai.nikin.typedgraph.core._
import munit.FunSuite

class GraphSpec extends FunSuite {
  test("Graph - simple edge") {
    val pipeline: Transformation[RecordA, RecordB] F_>>> Lake[RecordB] =
      transformation[RecordA, RecordB]("s1") >>> lake[RecordB]("t1")

    println(pipeline)

    testContained(
      compileErrors(
        """ transformation[RecordB, RecordA]("s1") >>> lake[RecordB]("t1") """
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

  test("Graph - lake to transform to lake") {
    val pipeline: Lake[RecordA] >>> Lake[RecordB] =
      lake[RecordA]("lA") >>> transformation[RecordA, RecordB]("tAB") >>> lake[RecordB]("lB")

    println(pipeline.inGraph.toDot)
    println(pipeline)
  }
}
