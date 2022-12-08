package ai.nikin.pipeline.interpreter

import ai.nikin.pipeline.interpreter.Definition.{AggregationDefinition, LakeDefinition}
import ai.nikin.pipeline.sdk._
import ai.nikin.pipeline.sdk.Aggregation.{Avg, Min}
import ai.nikin.pipeline.sdk.{aggregation, lake}
import zio.Scope
import zio.test.{assertTrue, Spec, TestEnvironment, ZIOSpecDefault}

object PipelineInterpreterSpec extends ZIOSpecDefault {

  @Schema
  case class RecordA(col1: String, col2: Int)

  @Schema
  case class RecordB(col1: String)

  @Schema
  case class RecordC(col1: Long)

  val ddlLakeA =
    s"""
       |CREATE TABLE IF NOT EXISTS RecordA(
       |    col1 STRING NOT NULL,
       |    col2 INT NOT NULL
       |) USING DELTA
       |    LOCATION s3a://BUCKET_PLACEHOLDER/RecordA
       |""".stripMargin

  val ddlLakeB =
    s"""
       |CREATE TABLE IF NOT EXISTS RecordB(
       |    col1 STRING NOT NULL
       |) USING DELTA
       |    LOCATION s3a://BUCKET_PLACEHOLDER/RecordB)
       |""".stripMargin

  val ddlLakeC =
    s"""
       |CREATE TABLE IF NOT EXISTS RecordC(
       |    col1 BIGINT NOT NULL
       |) USING DELTA
       |    LOCATION s3a://BUCKET_PLACEHOLDER/RecordC)
       |""".stripMargin

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("PipelineInterpreterSpec")(
      test("should correctly generate artifacts") {
        val lakeA    = lake[RecordA]("lA")
        val lakeB    = lake[RecordB]("lB")
        val lakeC    = lake[RecordC]("lC")
        val avg      = aggregation[RecordA, RecordB]("tAB", Avg("col1", "col2"))
        val min      = aggregation[RecordB, RecordC]("tBC", Min("col1", "col1"))
        val pipeline = lakeA >>> avg >>> lakeB >>> min >>> lakeC

        val artifacts = PipelineInterpreter.process(pipeline.asGraph)

        assertTrue(artifacts.get(lakeA.label).contains(LakeDefinition(ddlLakeA)))
        assertTrue(artifacts.get(lakeB.label).contains(LakeDefinition(ddlLakeB)))
        assertTrue(artifacts.get(lakeC.label).contains(LakeDefinition(ddlLakeC)))
        assertTrue(
          artifacts
            .get(avg.label)
            .contains(
              AggregationDefinition(
                lakeA.name,
                lakeB.name,
                "Avg",
                "col1",
                "col2"
              )
            )
        )
        assertTrue(
          artifacts
            .get(min.label)
            .contains(
              AggregationDefinition(
                lakeB.name,
                lakeC.name,
                "Min",
                "col1",
                "col1"
              )
            )
        )
      }
    )
}
