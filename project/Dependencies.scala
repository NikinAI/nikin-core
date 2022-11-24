import sbt._

object Dependencies {

  object Spark {
    lazy val sql = "org.apache.spark" %% "spark-sql" % "3.3.1"
  }

  object DeltaLake {
    lazy val core = "io.delta" %% "delta-core" % "2.1.1"
  }

  object Hadoop {
    lazy val aws = "org.apache.hadoop" % "hadoop-aws" % "3.3.4"
  }

  object Logging {
    lazy val `log4j-over-slf4j` = "org.slf4j"      % "log4j-over-slf4j" % "2.0.4"
    lazy val logback            = "ch.qos.logback" % "logback-classic"  % "1.4.5"
  }

}
