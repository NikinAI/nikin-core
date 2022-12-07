package ai.nikin.pipeline.deployment.gha.models

case class SparkApp(name: String, mainClass: String, inputPath: String, outputPath: String)
