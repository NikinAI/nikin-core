package ai.nikin.pipeline.interpreter

sealed trait Artifact

object Artifact {
  case class LakeArtifact(ddl: String) extends Artifact
  case class AggregationArtifact() extends Artifact
}
