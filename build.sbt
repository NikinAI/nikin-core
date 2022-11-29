import Dependencies._

ThisBuild / scalaVersion     := "2.13.10"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "ai.nikin"
ThisBuild / organizationName := "NikinAI"

lazy val root = (project in file("."))
  .settings(
    name := "nikin-core"
  )
  .aggregate(
    `pipeline-sdk`,
    `pipeline-interpreter`,
    `pipeline-deployment-gha`
  )

lazy val `pipeline-sdk` = project in file("./pipeline-sdk")

lazy val `pipeline-interpreter` = (project in file("./pipeline-interpreter"))
  .settings(
    libraryDependencies ++= List(ZIO.schema, ZIO.schemaDerivation, ZIO.test, Spark.sql % "test")
  )
  .dependsOn(`pipeline-sdk`)

lazy val `pipeline-deployment-gha` = (project in file("./pipeline-deployment-gha"))
  .dependsOn(`pipeline-interpreter`)

addCommandAlias("runScalafmt", ";scalafmt;scalafmtSbt")
