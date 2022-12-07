package ai.nikin.pipeline.deployment.gha

import ai.nikin.pipeline.deployment.gha.models.DeltaLakeKeeper
import ai.nikin.pipeline.deployment.gha.utils.DeploymentFilePresets

object Main extends DeploymentFilePresets {
  def main(args: Array[String]): Unit =
    println(
      workflowHeader("myWorkflow", Seq("main")) + runDeltaLakeKeeper(DeltaLakeKeeper("Repo")) +
        deployJarToS3 +
        runSparkApp("J-CLUSTERID", "AggregationJob", "ai.nikin.sparkjob.Main", "s3://bucket/jar/")
    )
}
