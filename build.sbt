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
    `pipeline-deployment-ga`
  )

lazy val `pipeline-sdk` = project in file("./pipeline-sdk")

lazy val `pipeline-interpreter` = (project in file("./pipeline-interpreter"))
  .dependsOn(`pipeline-sdk`)

lazy val `pipeline-deployment-ga` = (project in file("./pipeline-deployment-ga"))
  .dependsOn(`pipeline-interpreter`)

addCommandAlias("runScalafmt", ";scalafmt;scalafmtSbt")
