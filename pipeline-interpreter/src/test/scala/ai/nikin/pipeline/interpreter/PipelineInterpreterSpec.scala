package ai.nikin.pipeline.interpreter

import ai.nikin.pipeline.interpreter.Definition.{
  DeltaLakeTableDefinition,
  SparkAggregatorDefinition
}
import ai.nikin.pipeline.sdk._
import ai.nikin.pipeline.sdk.Aggregation.{Avg, Min}
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
    s"""CREATE TABLE IF NOT EXISTS lA(
       |    col1 STRING NOT NULL,
       |    col2 INT NOT NULL
       |) USING DELTA
       |    LOCATION s3a://BUCKET_PLACEHOLDER/lA""".stripMargin

  val ddlLakeB =
    s"""CREATE TABLE IF NOT EXISTS lB(
       |    col1 STRING NOT NULL
       |) USING DELTA
       |    LOCATION s3a://BUCKET_PLACEHOLDER/lB""".stripMargin

  val ddlLakeC =
    s"""CREATE TABLE IF NOT EXISTS lC(
       |    col1 BIGINT NOT NULL
       |) USING DELTA
       |    LOCATION s3a://BUCKET_PLACEHOLDER/lC""".stripMargin

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("PipelineInterpreterSpec")(
      test("should correctly generate artifacts") {
        val lakeA    = lake[RecordA]("lA")
        val lakeB    = lake[RecordB]("lB")
        val lakeC    = lake[RecordC]("lC")
        val avg      = aggregation[RecordA, RecordB]("tAB", Avg("col1", "col2"))
        val min      = aggregation[RecordB, RecordC]("tBC", Min("col1", "col1"))
        val pipeline = lakeA >>> avg >>> lakeB >>> min >>> lakeC

        val artifacts = PipelineInterpreter.process(pipeline)

        def assertLake(name: String, ddl: String) =
          assertTrue(
            artifacts.find(_.name == name).contains(DeltaLakeTableDefinition(name, ddl))
          )

        assertLake(lakeA.name, ddlLakeA) &&
        assertLake(lakeB.name, ddlLakeB) &&
        assertLake(lakeC.name, ddlLakeC) &&
        assertTrue(
          artifacts
            .find(_.name == "tAB")
            .contains(
              SparkAggregatorDefinition(
                "tAB",
                lakeA.name,
                lakeB.name,
                "avg",
                "col1",
                "col2"
              )
            )
        ) &&
        assertTrue(
          artifacts
            .find(_.name == "tBC")
            .contains(
              SparkAggregatorDefinition(
                "tBC",
                lakeB.name,
                lakeC.name,
                "min",
                "col1",
                "col1"
              )
            )
        )
      }
    )
}
