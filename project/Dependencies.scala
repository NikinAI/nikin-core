import sbt.Keys.{libraryDependencies, scalaVersion}
import sbt._

object Dependencies {
  object Spark {
    lazy val sql = "org.apache.spark" %% "spark-sql" % "3.3.1"
  }

  object DeltaLake {
    lazy val core = "io.delta" %% "delta-core" % "2.1.1"
  }

  object ZIO {
    lazy val schema           = "dev.zio" %% "zio-schema"            % "0.3.1"
    lazy val schemaDerivation = "dev.zio" %% "zio-schema-derivation" % "0.3.1"
    lazy val test             = "dev.zio" %% "zio-test"              % "2.0.4" % Test
    lazy val testSbt          = "dev.zio" %% "zio-test-sbt"          % "2.0.4" % Test
  }

  object Hadoop {
    lazy val aws = "org.apache.hadoop" % "hadoop-aws" % "3.3.4"
  }

  object Logging {
    lazy val `log4j-over-slf4j` = "org.slf4j"      % "log4j-over-slf4j" % "2.0.4"
    lazy val logback            = "ch.qos.logback" % "logback-classic"  % "1.4.5"
  }

  object TypedGraph {
    lazy val core = "ai.nikin" %% "typed-graph" % "0.1.2"
  }

}
