import Dependencies._

ThisBuild / scalaVersion     := "2.13.10"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "ai.nikin"
ThisBuild / organizationName := "NikinAI"
ThisBuild / scalacOptions ++=
  Seq(
    "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
    "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
    "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
    "-Xlint:infer-any", // Warn when a type argument is inferred to be Any.
    "-Xlint:constant", // Evaluation of a constant arithmetic expression results in an error.
    "-Ywarn-unused:imports", "-Xfatal-warnings", "-deprecation", "-Ywarn-dead-code",
    "-Ywarn-unused:params", "-Ywarn-unused:locals", "-Ywarn-value-discard", "-Ywarn-unused:privates"
  )
lazy val root = (project in file("."))
  .settings(
    name := "nikin-core"
  )
  .aggregate(
    `pipeline-sdk`,
    `pipeline-interpreter`,
    `pipeline-deployment-gha`
  )

lazy val `pipeline-sdk` =
  project
    .in(file("./pipeline-sdk"))
    .settings(
      libraryDependencies ++= Seq(TypedGraph.core, ZIO.schema, ZIO.schemaDerivation)
    )

lazy val `pipeline-interpreter` = (project in file("./pipeline-interpreter"))
  .settings(
    libraryDependencies ++= List(ZIO.schema, ZIO.schemaDerivation, ZIO.test, ZIO.testSbt),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
  .dependsOn(`pipeline-sdk`)

lazy val `pipeline-deployment-gha` = (project in file("./pipeline-deployment-gha"))
  .dependsOn(`pipeline-interpreter`)

addCommandAlias("runScalafmt", ";scalafmt;scalafmtSbt")
