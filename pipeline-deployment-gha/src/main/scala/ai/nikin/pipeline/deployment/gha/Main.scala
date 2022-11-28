package ai.nikin.pipeline.deployment.gha

import ai.nikin.pipeline.deployment.gha.models.DeltaTable
import ai.nikin.pipeline.deployment.gha.utils.DeploymentFilePresets

object Main extends DeploymentFilePresets {
  def main(args: Array[String]): Unit =
    println(
      workflowHeader("myWorkflow", Seq("main")) +
        generateDeltaTable(DeltaTable("Repo", "RepoPath")) + deployJarToS3 +
        runSparkJob("J-CLUSTERID", "AggregationJob", "ai.nikin.sparkjob.Main", "s3://bucket/jar/")
    )
}
