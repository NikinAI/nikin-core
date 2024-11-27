import sbt._

object Dependencies {

  object Versions {
    val `zio-core`   = "2.1.13"
    val `zio-schema` = "1.5.0"
    val `zio-json`   = "0.7.3"
  }

  object Scala {
    def reflect(scalaVersion: String): ModuleID = "org.scala-lang" % "scala-reflect" % scalaVersion
  }

  object ZIO {
    lazy val core             = "dev.zio" %% "zio"                   % Versions.`zio-core`
    lazy val schema           = "dev.zio" %% "zio-schema"            % Versions.`zio-schema`
    lazy val schemaDerivation = "dev.zio" %% "zio-schema-derivation" % Versions.`zio-schema`
    lazy val jsonMacros      = "dev.zio" %% "zio-json-macros"       % Versions.`zio-json`
    lazy val test             = "dev.zio" %% "zio-test"              % Versions.`zio-core` % Test
    lazy val testSbt          = "dev.zio" %% "zio-test-sbt"          % Versions.`zio-core` % Test
  }

  object Logging {
    lazy val `log4j-over-slf4j` = "org.slf4j"      % "log4j-over-slf4j" % "2.0.16"
    lazy val logback            = "ch.qos.logback" % "logback-classic"  % "1.5.12"
  }

  object Scalameta {
    lazy val munit = "org.scalameta" %% "munit" % "1.0.2"
  }

  object ScalaGraph {
    lazy val core = "org.scala-graph" %% "graph-core" % "2.0.2"
  }

  object Scalaland {
    lazy val chimney = "io.scalaland" %% "chimney" % "1.5.0"
  }

}
