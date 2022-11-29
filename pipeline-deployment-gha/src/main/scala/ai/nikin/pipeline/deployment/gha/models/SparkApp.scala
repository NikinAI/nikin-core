package ai.nikin.pipeline.deployment.gha.models

case class SparkApp(jobName: String, mainClass: String, inputPath: String, outputPath: String)
