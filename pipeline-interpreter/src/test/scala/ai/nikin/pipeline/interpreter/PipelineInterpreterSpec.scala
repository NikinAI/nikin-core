package ai.nikin.pipeline.interpreter

import ai.nikin.pipeline.sdk._
import ai.nikin.pipeline.sdk.Aggregation.Avg
import ai.nikin.pipeline.sdk.{aggregation, lake}
import zio.Scope
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}

class PipelineInterpreterSpec extends ZIOSpecDefault {

  @Schema
  case class RecordA(col1: String, col2: Int)
  @Schema
  case class RecordB(col1: String)

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("PipelineInterpreterSpec")(
      test("should correctly generate lake artifacts") {
        val pipeline =
          lake[RecordA]("lA") >>> aggregation[RecordA, RecordB]("tAB", Avg("col1", "col2")) >>>
            lake[RecordB]("lB")

        val artifacts = PipelineInterpreter.process(pipeline.inGraph)

        println(s"artifacts = $artifacts")
        assertTrue(artifacts.nonEmpty)
      }
    )
}
