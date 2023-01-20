import Dependencies._

ThisBuild / scalaVersion     := Versions.scala
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
    "-Ywarn-unused:params", "-Ywarn-unused:locals", "-Ywarn-value-discard",
    "-Ywarn-unused:privates", "-Ymacro-annotations"
  )

ThisBuild / resolvers += "GitHub Package Registry (NikinAI/TypedGraph)" at
  "https://maven.pkg.github.com/NikinAI/TypedGraph"

ThisBuild / credentials +=
  Credentials(
    realm = "GitHub Package Registry",
    host = "maven.pkg.github.com",
    userName = "_",
    passwd = {
      import scala.util.Try
      import scala.sys.process._
      sys.env.getOrElse("GITHUB_TOKEN", Try(s"git config github.token".!!).map(_.trim).get)
    }
  )

lazy val root = (project in file("."))
  .settings(
    name := "nikin-core"
  )
  .aggregate(
    `pipeline-dsl-macros`,
    `pipeline-sdk`,
    `pipeline-interpreter`,
    `pipeline-deployment-gha`
  )

lazy val `pipeline-dsl-macros` =
  project
    .in(file("./pipeline-dsl-macros"))
    .settings(
      libraryDependencies ++=
        Seq(
          Scala.reflect
        )
    )

lazy val `pipeline-sdk` =
  project
    .in(file("./pipeline-sdk"))
    .settings(
      libraryDependencies ++= Seq(TypedGraph.core, ZIO.schema, ZIO.schemaDerivation, "io.scalaland" %% "chimney" % "0.6.2")
    )
    .dependsOn(`pipeline-dsl-macros`)

lazy val `pipeline-interpreter` = (project in file("./pipeline-interpreter"))
  .settings(
    libraryDependencies ++= List(ZIO.schema, ZIO.schemaDerivation, ZIO.test, ZIO.testSbt)
  )
  .dependsOn(`pipeline-sdk`)

lazy val `pipeline-deployment-gha` = (project in file("./pipeline-deployment-gha"))
  .dependsOn(`pipeline-interpreter`)

addCommandAlias("runScalafmt", ";scalafmt;scalafmtSbt")
