package ai.nikin.pipeline.sdk

import ai.nikin.pipeline.sdk.Aggregation.{Avg, Sum}
import ai.nikin.pipeline.sdk.model.{RecordA, RecordB}

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
}
