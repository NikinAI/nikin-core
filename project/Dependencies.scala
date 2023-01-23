import sbt._

object Dependencies {

  object Versions {
    val `zio-core`   = "2.0.6"
    val `zio-schema` = "0.4.2"
  }

  object Scala {
    def reflect(scalaVersion: String): ModuleID = "org.scala-lang" % "scala-reflect" % scalaVersion
  }

  object ZIO {
    lazy val core             = "dev.zio" %% "zio"                   % Versions.`zio-core`
    lazy val schema           = "dev.zio" %% "zio-schema"            % Versions.`zio-schema`
    lazy val schemaDerivation = "dev.zio" %% "zio-schema-derivation" % Versions.`zio-schema`
    lazy val test             = "dev.zio" %% "zio-test"              % Versions.`zio-core` % Test
    lazy val testSbt          = "dev.zio" %% "zio-test-sbt"          % Versions.`zio-core` % Test
  }

  object Logging {
    lazy val `log4j-over-slf4j` = "org.slf4j"      % "log4j-over-slf4j" % "2.0.5"
    lazy val logback            = "ch.qos.logback" % "logback-classic"  % "1.4.5"
  }

  object TypedGraph {
    lazy val core = "ai.nikin" %% "typed-graph" % "0.1.2"
  }

  object Scalaland {
    lazy val chimney = "io.scalaland" %% "chimney" % "0.6.2"
  }

}
